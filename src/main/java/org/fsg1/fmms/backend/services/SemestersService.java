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
                        "  alrow as (select row_number() over () as num, id from study.architecturallayer), " +
                        "  acrow as (select row_number() over () as num, id from study.activity), " +
                        "  activities as (select array_to_json(array_agg(json_build_object('id', id, 'name', name, 'description', description))) as json from study.activity), " +
                        "  als as (select array_to_json(array_agg(json_build_object('id', id, 'name', name, 'description', description))) as json from study.architecturallayer), " +
                        "  modules as (select p.studyprogramme_id as sp, mp.semester as s, array_to_json(array_agg(json_build_object('code', m.code, 'name', m.name, 'credits', m.credits, 'is_project', 0))) as json from study.module as m inner join study.module_profile as mp on mp.module_id = m.id inner join study.profile as p on p.id = mp.profile_id group by p.studyprogramme_id, mp.semester) " +
                        "select json_build_object( " +
                        "  'curriculum_name', sp.name, " +
                        "  'modules', (select json from modules where sp = sp.id and s = mp.semester), " +
                        "  'lifecycle_activities', (select json from activities), " +
                        "  'architectural_layers', (select json from als), " +
                        "  'qualifications', " +
                        "  (select array_to_json(array_agg(json)) from ( " +
                        "  SELECT json_build_object('lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE id = q.activity_id), 'architectural_layer', (SELECT (num - 1) FROM alrow WHERE id = q.architecturallayer_id), 'level', max(los.level)) AS json " +
                        "  FROM study.learninggoal AS lg INNER JOIN study.learninggoal_qualification AS lg2q ON lg2q.learninggoal_id = lg.id INNER JOIN study.qualification AS q ON lg2q.qualification_id = q.id INNER JOIN study.levelofskill AS los ON los.id = q.levelofskill_id INNER JOIN study.module_profile AS mp ON mp.module_id = lg.module_id INNER JOIN study.profile AS p ON p.id = mp.profile_id " +
                        "  WHERE p.studyprogramme_id = ? AND semester <= ? " +
                        "  GROUP BY q.activity_id, q.architecturallayer_id " +
                        ") as tmp) " +
                        ") from study.module_profile as mp " +
                        "  inner join study.profile as p on p.id = mp.profile_id " +
                        "  inner join  study.studyprogramme as sp on sp.id = p.studyprogramme_id " +
                        "  where sp.id = ? and mp.semester = ? " +
                        "group by sp.id, sp.name, mp.semester;";
    }


}
