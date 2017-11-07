package org.fsg1.fmms.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fsg1.fmms.backend.database.Connection;
import org.fsg1.fmms.backend.exceptions.EntityNotFoundException;

import javax.inject.Inject;
import java.sql.ResultSet;

/**
 * The service class for the curriculum endpoint.
 */
public class CurriculumService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    CurriculumService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves every semester in a curriculum.
     *
     * @return Query string.
     */
    public String getQueryCurriculumSemestersString() {
        return
                "WITH " +
                        "modules AS (SELECT Array_to_json(Array_agg(Json_build_object('module_code', co.module_code, 'module_name', co.module_name, 'credits', co.credits))) AS json, study_programme_id AS sid, semester AS s FROM study.curriculum_overview AS co GROUP BY study_programme_id, semester), " +
                        "semesters AS ( " +
                        "      SELECT Json_build_object('semester', co2.semester, 'modules', (SELECT json FROM modules WHERE sid = co2.study_programme_id AND s = co2.semester)) AS json, co2.study_programme_id AS programme FROM study.curriculum_overview AS co2 GROUP BY co2.study_programme_id, co2.semester ORDER BY co2.semester) " +
                        " " +
                        "SELECT Json_build_object( " +
                        "  'semesters', (Array_to_json(Array_agg(json))) " +
                        ") AS semesters FROM semesters WHERE programme = ?;";
    }

    /**
     * Get the query string that retrieves the information of a module.
     *
     * @return Query string.
     */
    public String getQueryModuleInformation() {
        return
                "WITH " +
                        "    prior AS (SELECT Json_build_object('module_code',m.code , 'module_name', m.name, 'type', (CASE WHEN md.mandatory = TRUE THEN 'mandatory' WHEN md.concurrent THEN 'concurrent' ELSE 'previous' END), 'remarks', Coalesce(md.remarks, '')) AS prior_modules, md.module_id AS MODULE FROM study.moduledependency AS md inner join study.MODULE AS m ON m.id = md.dependency_module_id), " +
                        "    alrow AS (SELECT Row_number() over () AS num, id FROM study.architecturallayer), " +
                        "    acrow AS (SELECT Row_number() over () AS num, id FROM study.activity), " +
                        "    astype AS (SELECT Array_to_json(Array_agg(DISTINCT at.name)) AS names, as2lg.learninggoal_id AS lg FROM study.moduleasssementtype AS AT inner join study.moduleassessment_moduleassessmenttype AS ma2at ON ma2at.moduleassessmenttype_id = at.id inner join study.moduleassessment_learninggoal AS as2lg ON ma2at.moduleassessment_id = as2lg.moduleassessment_id GROUP BY as2lg.learninggoal_id), " +
                        "    skills AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer', (SELECT (num - 1) FROM alrow WHERE alrow.id = q.architecturallayer_id), 'lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE acrow.id = q.activity_id), 'level', los.LEVEL))) AS json, lq.learninggoal_id FROM study.learninggoal_qualification AS lq inner join study.qualification AS q ON q.id = lq.qualification_id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lq.learninggoal_id), " +
                        "    lg AS (SELECT Array_to_json(Array_agg(Json_build_object('name', Concat('LG ', sequenceno), 'description', description, 'type', (CASE lg.groupgoal WHEN TRUE THEN 'group' ELSE 'personal' END), 'weight', weight, 'assesment_types', (SELECT Coalesce(names, '[]'::json) FROM astype WHERE lg = lg.id), 'skillmatrix', Coalesce((SELECT json FROM skills WHERE skills.learninggoal_id = lg.id), '[]'::json)))) AS json, lg.module_id FROM study.learninggoal AS lg GROUP BY module_id), " +
                        "    activities AS (SELECT Array_to_json(Array_agg(Json_build_object('lifecycle_activity_id', id, 'lifecycle_activity_name', name, 'lifecycle_activity_description', description))) AS json FROM study.activity), " +
                        "    als AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer_id', id, 'architectural_layer_name', name, 'architectural_layer_description', description))) AS json FROM study.architecturallayer), " +
                        "    topics AS (SELECT array_to_json(array_agg(t.description order BY t.sequenceno)) AS topics, t.module_id AS MODULE FROM study.moduletopic AS t GROUP BY t.module_id), " +
                        "    moduleskills AS (SELECT lg.module_id AS MODULE, json_build_object('lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE id = q.activity_id), 'architectural_layer', (SELECT (num - 1) FROM alrow WHERE id = q.architecturallayer_id), 'level', max(los.LEVEL)) AS json FROM study.learninggoal AS lg inner join study.learninggoal_qualification AS lg2q ON lg2q.learninggoal_id = lg.id inner join study.qualification AS q ON lg2q.qualification_id = q.id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lg.module_id, q.activity_id, q.architecturallayer_id), " +
                        "    lecturers AS (SELECT array_to_json(array_agg(concat(e.firstname, ' ', e.lastname))) AS json, me.module_id AS MODULE FROM study.module_employee AS me inner join study.employee AS e ON e.id = me.employee_id GROUP BY me.module_id) " +
                        "SELECT json_build_object( " +
                        "  'module_code', m.code, " +
                        "  'module_name', m.name, " +
                        "  'credits', m.credits, " +
                        "  'credentials', coalesce(md.credentials, ''), " +
                        "  'lecturers', (SELECT json FROM lecturers WHERE MODULE = m.id), " +
                        "  'lifecycle_activities', (SELECT json FROM activities), " +
                        "  'architectural_layers', (SELECT json FROM als), " +
                        "  'learning_goals', (SELECT json FROM lg WHERE lg.module_id = m.id), " +
                        "  'lectures_in_week', m.lecturesperweek, " +
                        "  'practical_hours_week', m.practicalperweek, " +
                        "  'total_effort', m.totaleffort, " +
                        "  'introductorytext', coalesce(md.introduction, ''), " +
                        "  'teaching_material', coalesce(md.teachingmaterial, ''), " +
                        "  'additional_information', coalesce(md.additionalinfo, ''), " +
                        "  'topics', (SELECT coalesce(topics, '[]'::json) FROM topics WHERE MODULE = m.id), " +
                        "  'semester', mp.semester, " +
                        "  'prior_knowledge_references', (SELECT coalesce(array_to_json(array_agg(PRIOR.prior_modules)), '[]'::json) FROM PRIOR WHERE PRIOR.MODULE = m.id), " +
                        "  'qualifications', (SELECT array_to_json(array_agg(json)) FROM moduleskills WHERE MODULE = m.id) " +
                        ") as module FROM study.module AS m " +
                        "  left join study.moduledescription AS md ON md.module_id = m.id " +
                        "  left join study.module_profile AS mp ON mp.module_id = m.id " +
                        "  left join study.PROFILE AS p ON mp.profile_id = p.id " +
                        "WHERE m.code = ? AND p.studyprogramme_id = ?";
    }

    /**
     * {@inheritDoc}
     * Gets all semesters and their modules in a given curriculum.
     *
     * @param parameters The first parameter should be the identifier of the curriculum.
     * @return A JSON ObjectNode of the resulting JSON object.
     */
    @Override
    public JsonNode get(final String query, final String columnName, final Object... parameters) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try (ResultSet resultSet = getConn().executeQuery(query, parameters)) {
            if (!resultSet.next()) throw new EntityNotFoundException();
            final String jsonString = resultSet.getString(columnName);
            return mapper.readTree(jsonString);
        }
    }

    /**
     * Get the query string that retrieves a complete semester.
     *
     * @return Query string
     */
    public String getQueryCompleteSemester() {
        return
                "";
    }
}
