package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

/**
 * The service class for the architecturallayer/activity endpoint.
 */
public class LayerActivityService extends Service {
    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    LayerActivityService(final Connection connection) {
        super(connection);
    }

    /**
     * Get the query string that retrieves the overview for a qualification.
     *
     * @return The query string.
     */
    public String getQueryQualificationsOverview() {
        return
                "WITH " +
                        "learninggoals AS ( " +
                        "      SELECT " +
                        "        Json_build_object( " +
                        "            'name', Concat('LG ', lg.sequenceno), " +
                        "            'description', lg.description " +
                        "        ) AS json, " +
                        "        los.level AS level, " +
                        "        lg.module_id AS module, " +
                        "        lg.sequenceno AS seq " +
                        "      FROM study.learninggoal AS lg " +
                        "        inner join study.learninggoal_qualification AS lq ON lq.learninggoal_id = lg.id " +
                        "        inner join study.qualification AS q ON q.id = lq.qualification_id " +
                        "        inner join study.levelofskill AS los ON los.id = q.levelofskill_id " +
                        "      WHERE q.architecturallayer_id = ? AND q.activity_id = ? " +
                        "  ), " +
                        "  modules AS ( " +
                        "      SELECT " +
                        "        m.id      AS module, " +
                        "        lgs.level AS level, " +
                        "        m.code AS code, " +
                        "        json_build_object( " +
                        "            'module_code', m.code, " +
                        "            'module_name', m.name, " +
                        "            'credits', m.credits, " +
                        "            'learning_goals', array_agg(lgs.json order BY lgs.seq) " +
                        "        )         AS json " +
                        "      FROM study.module AS m " +
                        "        right join learninggoals AS lgs ON lgs.module = m.id " +
                        "      GROUP BY lgs.level, m.id, m.code, m.name, m.credits " +
                        "  ), " +
                        "  semesters AS ( " +
                        "      SELECT " +
                        "        ms.level AS level, " +
                        "        mp.semester AS semester, " +
                        "        json_build_object( " +
                        "            'semester', mp.semester, " +
                        "            'qualifications_modules', array_agg(ms.json ORDER BY ms.code) " +
                        "        ) AS json " +
                        "      FROM " +
                        "        study.module_profile AS mp " +
                        "        inner join study.profile AS p ON mp.profile_id = p.id " +
                        "        right join modules AS ms ON ms.module = mp.module_id " +
                        "      WHERE p.studyprogramme_id = ? " +
                        "      GROUP BY ms.level, mp.semester " +
                        "  ), " +
                        "  skills AS ( " +
                        "      SELECT " +
                        "        json_build_object( " +
                        "          'skills_level', s.level, " +
                        "          'qualifications_overview_semesters', array_agg(s.json ORDER BY s.semester) " +
                        "        ) AS json " +
                        "      FROM " +
                        "        semesters AS s " +
                        "      GROUP BY s.level " +
                        "      ORDER BY s.level " +
                        "  ) " +
                        "SELECT " +
                        "  array_to_json(array_agg(json)) as qualifications_overview " +
                        "FROM skills";
    }
}
