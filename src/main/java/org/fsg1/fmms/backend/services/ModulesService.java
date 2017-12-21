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
                        "    prior AS (SELECT Json_build_object('id', m.id, 'code',m.code , 'name', m.name, 'type', lower(md.type::text), 'remarks', Coalesce(md.remarks, '')) AS prior_modules, md.module_id AS module FROM study.moduledependency AS md inner join study.module AS m ON m.id = md.dependency_module_id), " +
                        "    alrow AS (SELECT Row_number() over () AS num, id FROM study.architecturallayer), " +
                        "    acrow AS (SELECT Row_number() over () AS num, id FROM study.activity), " +
                        "    material AS (SELECT Array_agg(tm.description) AS descs, tm.moduledescription_id AS md_id FROM study.teachingmaterial AS tm GROUP BY tm.moduledescription_id), " +
                        "    skills AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer', (SELECT (num - 1) FROM alrow WHERE alrow.id = q.architecturallayer_id), 'lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE acrow.id = q.activity_id), 'level', los.LEVEL))) AS json, lq.learninggoal_id FROM study.learninggoal_qualification AS lq inner join study.qualification AS q ON q.id = lq.qualification_id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lq.learninggoal_id), " +
                        "    lg AS (SELECT Array_to_json(Array_agg(Json_build_object('name', Concat('LG ', sequenceno), 'description', description, 'type', (CASE lg.groupgoal WHEN TRUE THEN 'group' ELSE 'personal' END), 'skillmatrix', Coalesce((SELECT json FROM skills WHERE skills.learninggoal_id = lg.id), '[]'::json)) order by lg.sequenceno)) AS json, lg.module_id FROM study.learninggoal AS lg GROUP BY module_id), " +
                        "    activities AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.activity), " +
                        "    als AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.architecturallayer), " +
                        "    topics AS (SELECT array_to_json(array_agg(t.description order BY t.sequenceno)) AS topics, t.module_id AS module FROM study.moduletopic AS t GROUP BY t.module_id), " +
                        "    moduleskills AS (SELECT lg.module_id AS module, json_build_object('lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE id = q.activity_id), 'architectural_layer', (SELECT (num - 1) FROM alrow WHERE id = q.architecturallayer_id), 'level', max(los.LEVEL)) AS json FROM study.learninggoal AS lg inner join study.learninggoal_qualification AS lg2q ON lg2q.learninggoal_id = lg.id inner join study.qualification AS q ON lg2q.qualification_id = q.id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lg.module_id, q.activity_id, q.architecturallayer_id), " +
                        "    lecturers AS (SELECT array_to_json(array_agg(concat(e.firstname, ' ', e.lastname))) AS json, me.module_id AS module FROM study.module_employee AS me inner join study.employee AS e ON e.id = me.employee_id GROUP BY me.module_id), " +
                        "    grading AS (SELECT array_agg(json_build_object('subcode', ma.code, 'description', ma.description, 'percentage', coalesce(ma.weight, 0.0), 'minimal_grade', ma.minimumgrade, 'remark', coalesce(ma.remarks, '')) ORDER BY ma.code) AS json, ma.module_id AS module FROM study.moduleassessment AS ma GROUP BY ma.module_id) " +
                        "SELECT json_build_object( " +
                        "  'id', m.id, " +
                        "  'code', m.code, " +
                        "  'name', m.name, " +
                        "  'credits', m.credits, " +
                        "  'credentials', coalesce(md.credentials, ''), " +
                        "  'lecturers', coalesce((SELECT json FROM lecturers WHERE module = m.id), '[]'::json), " +
                        "  'lifecycle_activities', (SELECT json FROM activities), " +
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
                        ") as module FROM study.module AS m " +
                        "  left join study.moduledescription AS md ON md.module_id = m.id " +
                        "  left join study.module_profile AS mp ON mp.module_id = m.id " +
                        "  left join study.profile AS p ON mp.profile_id = p.id " +
                        "WHERE m.code = ? AND p.studyprogramme_id = ?;";
    }

    /**
     * Get the queries to update module information.
     *
     * @return The query string.
     */
    public String[] getUpdateModuleInformationStatements() {
        return new String[]{
                //Index 0: update base module
                "UPDATE study.module " +
                        "    SET code = ?, name = ?, credits = ?, lecturesperweek = ?, practicalperweek = ?, isproject = ?, totaleffort = credits * 28 " +
                        "WHERE id = ?",

                //index 1: delete topics linked to module
                "DELETE FROM study.moduletopic " +
                        "WHERE module_id = ?",

                //index 2: re-insert module topics
                "INSERT INTO study.moduletopic(module_id, sequenceno, description) " +
                        "    VALUES (?, NULL, ?)",

                //index 3: update module description
                "UPDATE study.moduledescription  " +
                        "  SET introduction = ?, additionalinfo = ?, credentials = ? " +
                        "WHERE module_id = ?;",

                //index 4: delete module teaching materials
                "DELETE FROM study.teachingmaterial " +
                        "WHERE moduledescription_id = (SELECT id FROM study.moduledescription WHERE module_id = ?); ",

                //index 5: re-insert module teaching materials
                "INSERT INTO study.teachingmaterial (moduledescription_id, type, description) " +
                        "    VALUES ((SELECT id FROM study.moduledescription WHERE module_id = ?), (upper(?)::study.teachingmaterials), ?)",

                //index 6: delete linked employees
                "DELETE FROM study.module_employee " +
                        "  WHERE module_id = ?",

                //index 7: re-insert employees linked to module
                "INSERT INTO study.module_employee(module_id, employee_id) " +
                        "  VALUES (?, ?)",

                //index 8: delete module's dependencies
                "DELETE FROM study.moduledependency " +
                        "  WHERE module_id = ?;",

                //index 9: re-insert module dependencies
                "INSERT INTO study.moduledependency(module_id, dependency_module_id, type, remarks) " +
                        "  VALUES (?, ?, (upper(?)::study.dependencytype), ?);",

                //index 10: delete studies learning goals.
                "DELETE FROM study.learninggoal " +
                        "  WHERE module_id = ?;",

                //index 11: insert learning goals
                "INSERT INTO study.learninggoal(module_id, description, sequenceno, weight, groupgoal) " +
                        "    VALUES (?, ?, NULL, ?, ?);",

                //index 12: link learning goals to qualifications
                "INSERT INTO study.learninggoal_qualification(learninggoal_id, qualification_id) " +
                        "    VALUES (?, (SELECT id FROM study.qualification WHERE architecturallayer_id = ? AND activity_id = ? AND levelofskill_id = ?)); ",

                //index 13: delete module assessments
                "DELETE FROM study.moduleassessment " +
                        "  WHERE module_id = ?;",

                //index 14: re insert module assessments
                "INSERT INTO study.moduleassessment(code, weight, minimumgrade, remarks, module_id, description) " +
                        "  VALUES (?, ?, ?, ?, ?, ?);"
        };
    }

    /**
     * Get the query string to get a module that has extra information for editing.
     *
     * @return The query string.
     */
    public String getQueryEditableModule() {
        return
                "WITH " +
                        "    prior AS (SELECT Json_build_object('id', m.id, 'code',m.code , 'name', m.name, 'type', (lower(md.type::text)), 'remarks', Coalesce(md.remarks, '')) AS prior_modules, md.module_id AS module  FROM study.moduledependency AS md inner join study.module  AS m ON m.id = md.dependency_module_id), " +
                        "    alrow AS (SELECT Row_number() over () AS num, id FROM study.architecturallayer), " +
                        "    acrow AS (SELECT Row_number() over () AS num, id FROM study.activity), " +
                        "    material AS (SELECT Array_agg(Json_build_object('name', tm.description, 'type', tm.TYPE)) AS json, tm.moduledescription_id AS md_id FROM study.teachingmaterial AS tm GROUP BY tm.moduledescription_id), " +
                        "    skills AS (SELECT Array_to_json(Array_agg(Json_build_object('architectural_layer', (SELECT (num - 1) FROM alrow WHERE alrow.id = q.architecturallayer_id), 'lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE acrow.id = q.activity_id), 'level', los.LEVEL))) AS json, lq.learninggoal_id FROM study.learninggoal_qualification AS lq inner join study.qualification AS q ON q.id = lq.qualification_id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lq.learninggoal_id), " +
                        "    lg AS (SELECT Array_to_json(Array_agg(Json_build_object('name', Concat('LG ', sequenceno), 'weight', weight::DECIMAL, 'description', description, 'type', (CASE lg.groupgoal WHEN TRUE THEN 'group' ELSE 'personal' END), 'skillmatrix', Coalesce((SELECT json FROM skills WHERE skills.learninggoal_id = lg.id), '[]'::json)))) AS json, lg.module_id FROM study.learninggoal AS lg GROUP BY module_id), " +
                        "    acitivies AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.activity), " +
                        "    als AS (SELECT Array_to_json(Array_agg(Json_build_object('id', id, 'name', name, 'description', description))) AS json FROM study.architecturallayer), " +
                        "    topics AS (SELECT array_to_json(array_agg(t.description order BY t.sequenceno)) AS topics, t.module_id AS module  FROM study.moduletopic AS t GROUP BY t.module_id), " +
                        "    moduleskills AS (SELECT lg.module_id AS module , json_build_object('lifecycle_activity', (SELECT (num - 1) FROM acrow WHERE id = q.activity_id), 'architectural_layer', (SELECT (num - 1) FROM alrow WHERE id = q.architecturallayer_id), 'level', max(los.LEVEL)) AS json FROM study.learninggoal AS lg inner join study.learninggoal_qualification AS lg2q ON lg2q.learninggoal_id = lg.id inner join study.qualification AS q ON lg2q.qualification_id = q.id inner join study.levelofskill AS los ON los.id = q.levelofskill_id GROUP BY lg.module_id, q.activity_id, q.architecturallayer_id), " +
                        "    all_lecturers AS (SELECT array_to_json(array_agg(json_build_object('id', id, 'name', concat(firstname, ' ', lastname)) ORDER BY lastname)) AS json FROM study.employee), " +
                        "    lecturers AS (SELECT array_to_json(array_agg(json_build_object('id', e.id, 'name', concat(firstname, ' ', lastname)) ORDER BY e.lastname)) AS json, me.module_id AS module  FROM study.module_employee AS me inner join study.employee AS e ON e.id = me.employee_id GROUP BY me.module_id), " +
                        "    grading AS (SELECT array_agg(json_build_object('subcode', ma.code, 'description', ma.description, 'percentage', coalesce(ma.weight, 0.0), 'minimal_grade', ma.minimumgrade, 'remark', coalesce(ma.remarks, '')) ORDER BY ma.code) AS json, ma.module_id AS module  FROM study.moduleassessment AS ma GROUP BY ma.module_id), " +
                        "    semesters AS (SELECT array_to_json(array_agg(DISTINCT mp.semester)) AS json, mp.module_id AS module  FROM study.module_profile AS mp GROUP BY mp.module_id), " +
                        "    modules AS (SELECT array_to_json(array_agg(json_build_object('id', id, 'code', code, 'name', name))) AS modules FROM study.module ) " +
                        "SELECT json_build_object( " +
                        "  'id', m.id, " +
                        "  'code', m.code, " +
                        "  'name', m.name, " +
                        "  'credits', m.credits, " +
                        "  'semesters',coalesce((SELECT json FROM semesters WHERE module  = m.id), '[]'::json), " +
                        "  'lectures_in_week', m.lecturesperweek, " +
                        "  'practical_hours_week', m.practicalperweek, " +
                        "  'introductorytext', coalesce(md.introduction, ''), " +
                        "  'topics', coalesce((SELECT topics FROM topics WHERE module  = m.id), '[]'::json), " +
                        "  'teaching_material', coalesce((SELECT array_to_json(mat.json) FROM material AS mat WHERE mat.md_id = md.id), '[]'::json), " +
                        "  'teaching_material_types', '[\"BOOK\", \"WEBSITE\", \"ARTICLE\", \"OTHER\"]'::json, " +
                        "  'additional_information', coalesce(md.additionalinfo, ''), " +
                        "  'all_lecturers', coalesce((SELECT json FROM all_lecturers), '[]'::json), " +
                        "  'active_lecturers', coalesce((SELECT json FROM lecturers WHERE module  = m.id), '[]'::json), " +
                        "  'credentials', coalesce(md.credentials, ''), " +
                        "  'project_flag', m.isproject, " +
                        "  'learning_goals', coalesce((SELECT json FROM lg WHERE lg.module_id = m.id), '[]'::json), " +
                        "  'assesment_parts', coalesce((SELECT array_to_json(json) FROM grading WHERE module  = m.id), '[]'::json), " +
                        "  'prior_knowledge_references', coalesce((SELECT array_to_json(array_agg(PRIOR.prior_modules)) FROM PRIOR WHERE PRIOR.module  = m.id), '[]'::json), " +
                        "  'modules', (SELECT modules FROM modules), " +
                        "  'lifecycle_activities', (SELECT json FROM acitivies), " +
                        "  'architectural_layers', (SELECT json FROM als), " +
                        "  'total_effort', coalesce(m.totaleffort, (m.credits * 28)), " +
                        "  'qualifications', coalesce((SELECT array_to_json(array_agg(json)) FROM moduleskills WHERE module  = m.id), '[]'::json) " +
                        ") AS module FROM study.module AS m " +
                        "  left join study.moduledescription AS md ON md.module_id = m.id " +
                        "WHERE m.code = ?;";
    }

    /**
     * @param moduleName     Name of the module.
     * @param semester       Semester of the module.
     * @param credits        Credits.
     * @param lectures       Lectures per week.
     * @param practicalHours Practical hours per week.
     * @param totalEffort    Total effort the module takes.
     * @param lecturers      Lecturers of the module.
     * @param credentials    Credentials. May be empty.
     * @return A LaTeX string with the main module information formatted.
     */
    public String latexHeader(final String moduleName,
                              final int semester,
                              final int credits,
                              final int lectures,
                              final int practicalHours,
                              final int totalEffort,
                              final String lecturers,
                              final String credentials) {
        String result =
                "\\begin{header}\n" +
                        "\t\\Module{" + moduleName + "}\n" +
                        "\t\\Semester{" + semester + "}\n" +
                        "\t\\Credits{" + credits + "}\n" +
                        "\t\\ValidOf{\\mydate}\n" +
                        "\t\\Lectures{" + lectures + "}\n" +
                        "\t\\Practical{" + practicalHours + "}\n" +
                        "\t\\TotalEffort{" + totalEffort + "}\n";

        if (!lecturers.isEmpty()) {
            result +=
                    "\t\\Authors{" + lecturers + "}\n";
        }

        if (!credentials.isEmpty()) {
            result +=
                    "\t\\Credentials{" + credentials + "}\n";
        }
        result += "\\end{header}\n";

        return result;
    }

    /**
     * @param introduction Introduction text.
     * @return a LaTeX string with the module introduction formatted.
     */
    public String latexIntroduction(final String introduction) {
        return
                "\\Introduction{" + introduction + "}\n";
    }

    /**
     * @param name        Name of the learning goal.
     * @param description Description.
     * @return a LaTeX string with one learning goal formatted.
     */
    public String latexLearningGoal(final String name, final String description) {
        return
                "\\LearningGoal{" + name + "}{" + description + "}\n";
    }

    /**
     * @param topic Topic name
     * @return a LaTeX string with one topic formatted.
     */
    public String latexTopic(final String topic) {
        return
                "\\Topic{" + topic + "}\n";
    }

    /**
     * @param subCode     ProgRESS subcode of the exam.
     * @param description Description.
     * @param percentage  Percentage weight.
     * @param minGrade    Minimal grade.
     * @return a LaTeX string with one assessment part formatted.
     */
    public String latexExam(
            final String subCode,
            final String description,
            final double percentage,
            final double minGrade) {
        return
                "\\Exam{" + subCode + "}{" + description + "}{" + percentage + "}{" + minGrade + "}\n";
    }

    /**
     * @param description Description.
     * @return a LaTeX string with a teaching material formatted.
     */
    public String latexTeachingMaterial(final String description) {
        return
                "\\Material{" + description + "}\n";
    }

    /**
     * @param code Code of the module that is linked.
     * @param name Name of the module.
     * @param type Type of link.
     * @return a LaTeX string with one module link formatted.
     */
    public String latexModuleLink(final String code, final String name, final String type) {
        return
                "\\Module{" + code + "}{" + name + "}{" + type + "}\n";
    }

    /**
     * @param information Additional information.
     * @return a LaTeX string with additional information formatted.
     */
    public String latexAdditionalInformation(final String information) {
        return
                "\\AdditionalInformation{" + information + "}\n";
    }
}
