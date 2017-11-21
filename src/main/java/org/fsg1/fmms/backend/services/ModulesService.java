package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

/**
 * Service class for the 'modules' endpoint.
 */
public class ModulesService extends Service {
    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    ModulesService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves the information of a module.
     *
     * @return Query string.
     */
    public String getQueryModuleInformation() {
        return
                "WITH " +
                        "    prior AS (SELECT Json_build_object('code',m.code , 'name', m.name, 'type', (CASE WHEN md.mandatory = TRUE THEN 'mandatory' WHEN md.concurrent THEN 'concurrent' ELSE 'previous' END), 'remarks', Coalesce(md.remarks, '')) AS prior_modules, md.module_id AS module FROM study.moduledependency AS md inner join study.module AS m ON m.id = md.dependency_module_id), " +
                        "    alrow AS (SELECT Row_number() over () AS num, id FROM study.architecturallayer), " +
                        "    acrow AS (SELECT Row_number() over () AS num, id FROM study.activity), " +
                        "    material AS (SELECT Array_agg(tm.description) AS descs, tm.moduledescription_id AS md_id FROM study.teachingmaterial AS tm GROUP BY tm.moduledescription_id), " +
                        "    skills AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer', (SELECT (num - 1) FROM alrow WHERE alrow.id = q.architecturallayer_id), 'lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE acrow.id = q.activity_id), 'level', los.LEVEL))) AS json, lq.learninggoal_id FROM study.learninggoal_qualification AS lq inner join study.qualification AS q ON q.id = lq.qualification_id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lq.learninggoal_id), " +
                        "    lg AS (SELECT Array_to_json(Array_agg(Json_build_object('name', Concat('LG ', sequenceno), 'description', description, 'type', (CASE lg.groupgoal WHEN TRUE THEN 'group' ELSE 'personal' END), 'skillmatrix', Coalesce((SELECT json FROM skills WHERE skills.learninggoal_id = lg.id), '[]'::json)))) AS json, lg.module_id FROM study.learninggoal AS lg GROUP BY module_id), " +
                        "    acitivies AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.activity), " +
                        "    als AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.architecturallayer), " +
                        "    topics AS (SELECT array_to_json(array_agg(t.description order BY t.sequenceno)) AS topics, t.module_id AS module FROM study.moduletopic AS t GROUP BY t.module_id), " +
                        "    moduleskills AS (SELECT lg.module_id AS module, json_build_object('lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE id = q.activity_id), 'architectural_layer', (SELECT (num - 1) FROM alrow WHERE id = q.architecturallayer_id), 'level', max(los.LEVEL)) AS json FROM study.learninggoal AS lg inner join study.learninggoal_qualification AS lg2q ON lg2q.learninggoal_id = lg.id inner join study.qualification AS q ON lg2q.qualification_id = q.id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lg.module_id, q.activity_id, q.architecturallayer_id), " +
                        "    lecturers AS (SELECT array_to_json(array_agg(concat(e.firstname, ' ', e.lastname))) AS json, me.module_id AS module FROM study.module_employee AS me inner join study.employee AS e ON e.id = me.employee_id GROUP BY me.module_id), " +
                        "    grading AS (SELECT array_agg(json_build_object('subcode', ma.code, 'description', ma.description, 'percentage', coalesce(ma.weight, 0.0), 'minimal_grade', ma.minimumgrade, 'remark', coalesce(ma.remarks, '')) ORDER BY ma.code) AS json, ma.module_id AS module FROM study.moduleassessment AS ma GROUP BY ma.module_id) " +
                        "SELECT json_build_object( " +
                        "  'code', m.code, " +
                        "  'name', m.name, " +
                        "  'credits', m.credits, " +
                        "  'credentials', coalesce(md.credentials, ''), " +
                        "  'lecturers', coalesce((SELECT json FROM lecturers WHERE module = m.id), '[]'::json), " +
                        "  'lifecycle_activities', (SELECT json FROM acitivies), " +
                        "  'architectural_layers', (SELECT json FROM als), " +
                        "  'learning_goals', coalesce((SELECT json FROM lg WHERE lg.module_id = m.id), '[]'::json), " +
                        "  'lectures_in_week', m.lecturesperweek, " +
                        "  'practical_hours_week', m.practicalperweek, " +
                        "  'total_effort', coalesce(m.totaleffort, (m.credits * 28)), " +
                        "  'introductorytext', coalesce(md.introduction, ''), " +
                        "  'additional_information', coalesce(md.additionalinfo, ''), " +
                        "  'topics', coalesce((SELECT topics FROM topics WHERE module = m.id), '[]'::json), " +
                        "  'semester', mp.semester, " +
                        "  'teaching_material', coalesce((SELECT array_to_json(mat.descs) FROM material AS mat WHERE mat.md_id = md.id), '[]'::json), " +
                        "  'prior_knowledge_references', coalesce((SELECT array_to_json(array_agg(PRIOR.prior_modules)) FROM PRIOR WHERE PRIOR.module = m.id), '[]'::json), " +
                        "  'qualifications', coalesce((SELECT array_to_json(array_agg(json)) FROM moduleskills WHERE module = m.id), '[]'::json), " +
                        "  'assesment_parts', coalesce((SELECT array_to_json(json) FROM grading WHERE module = m.id), '[]'::json) " +
                        ") FROM study.module AS m " +
                        "  left join study.moduledescription AS md ON md.module_id = m.id " +
                        "  left join study.module_profile AS mp ON mp.module_id = m.id " +
                        "  left join study.profile AS p ON mp.profile_id = p.id " +
                        "WHERE m.code = ? AND p.studyprogramme_id = ?;";
    }
}
