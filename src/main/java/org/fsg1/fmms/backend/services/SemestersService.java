package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

/**
 * Service class for the 'semesters' endpoint.
 */
public class SemestersService extends Service {
    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    SemestersService(final Connection connection) {
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
                        "modules AS (SELECT Array_to_json(Array_agg(Json_build_object('module_code', co.module_code, 'module_name', co.module_name, 'credits', co.credits, 'is_project', co.isproject) order by co.isproject, co.module_name)) AS json, study_programme_id AS sid, semester AS s FROM study.curriculum_overview AS co GROUP BY study_programme_id, semester), " +
                        "semesters AS ( " +
                        "      SELECT Json_build_object('semester', co2.semester, 'modules', (SELECT json FROM modules WHERE sid = co2.study_programme_id AND s = co2.semester)) AS json, co2.study_programme_id AS programme FROM study.curriculum_overview AS co2 GROUP BY co2.study_programme_id, co2.semester ORDER BY co2.semester) " +
                        " " +
                        "SELECT Json_build_object( " +
                        "  'semesters', (Array_to_json(Array_agg(json))) " +
                        ") AS semesters FROM semesters WHERE programme = ?;";
    }

    /**
     * Get the query string that retrieves a complete semester.
     *
     * @return Query string
     */
    public String getQueryCompleteSemester() {
        return
                "WITH " +
                        "  alrow AS (SELECT Row_number() over () AS num, id FROM study.architecturallayer), " +
                        "  acrow AS (SELECT Row_number() over () AS num, id FROM study.activity), " +
                        "  activities AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.activity), " +
                        "  als AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.architecturallayer), " +
                        "  modules AS (SELECT p.studyprogramme_id AS sp, mp.semester AS s, Array_to_json(Array_agg(Json_build_object('code', m.code, 'name', m.name, 'credits', m.credits, 'is_project', 0))) AS json FROM study.module AS m inner join study.module_profile AS mp ON mp.module_id = m.id inner join study.profile AS p ON p.id = mp.profile_id GROUP BY p.studyprogramme_id, mp.semester) " +
                        "SELECT Json_build_object( " +
                        "  'curriculum_name', sp.name, " +
                        "  'modules', (SELECT json FROM modules WHERE sp = sp.id AND s = mp.semester), " +
                        "  'lifecycle_activities', (SELECT json FROM activities), " +
                        "  'architectural_layers', (SELECT json FROM als), " +
                        "  'qualifications', " +
                        "  (SELECT Array_to_json(Array_agg(json)) FROM ( " +
                        "  SELECT Json_build_object('lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE id = q.activity_id), 'architectural_layer', (SELECT (num - 1) FROM alrow WHERE id = q.architecturallayer_id), 'level', Max(los.LEVEL)) AS json " +
                        "  FROM study.learninggoal AS lg inner join study.learninggoal_qualification AS lg2q ON lg2q.learninggoal_id = lg.id inner join study.qualification AS q ON lg2q.qualification_id = q.id inner join study.levelofskill AS los ON los.id = q.levelofskill_id inner join study.module_profile AS mp ON mp.module_id = lg.module_id inner join study.profile AS p ON p.id = mp.profile_id " +
                        "  WHERE p.studyprogramme_id = ? AND semester <= ? " +
                        "  GROUP BY q.activity_id, q.architecturallayer_id " +
                        ") AS tmp) " +
                        ") AS complete_semester FROM study.module_profile AS mp " +
                        "  inner join study.profile AS p ON p.id = mp.profile_id " +
                        "  inner join study.studyprogramme AS sp ON sp.id = p.studyprogramme_id " +
                        "  WHERE sp.id = ? AND mp.semester = ? " +
                        "GROUP BY sp.id, sp.name, mp.semester;";
    }


}
