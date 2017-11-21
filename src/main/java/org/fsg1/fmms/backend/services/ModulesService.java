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
                        "    prior as (select json_build_object('code',m.code , 'name', m.name, 'type', (case when md.mandatory = true then 'mandatory' when md.concurrent then 'concurrent' else 'previous' end), 'remarks', coalesce(md.remarks, '')) as prior_modules, md.module_id as module from study.moduledependency as md inner join study.module as m on m.id = md.dependency_module_id), " +
                        "    alrow as (select row_number() over () as num, id from study.architecturallayer), " +
                        "    acrow as (select row_number() over () as num, id from study.activity), " +
                        "    material as (select array_agg(tm.description) as descs, tm.moduledescription_id as md_id from study.teachingmaterial as tm group by tm.moduledescription_id), " +
                        "    skills as (select array_to_json(array_agg(json_build_object('architectural_layer', (select (num - 1) from alrow where alrow.id = q.architecturallayer_id), 'lifecycle_activity', (select (num - 1) from acrow where acrow.id = q.activity_id), 'level', los.level))) as json, lq.learninggoal_id from study.learninggoal_qualification as lq inner join study.qualification as q on q.id = lq.qualification_id inner join study.levelofskill as los on los.id = q.levelofskill_id group by lq.learninggoal_id), " +
                        "    lg as (select array_to_json(array_agg(json_build_object('name', concat('LG ', sequenceno), 'description', description, 'type', (case lg.groupgoal when true then 'group' else 'personal' end), 'skillmatrix', COALESCE((select json from skills where skills.learninggoal_id = lg.id), '[]'::JSON)))) as json, lg.module_id from study.learninggoal as lg group by module_id), " +
                        "    acitivies as (select array_to_json(array_agg(json_build_object('id', id, 'name', name, 'description', description))) as json from study.activity), " +
                        "    als as (select array_to_json(array_agg(json_build_object('id', id, 'name', name, 'description', description))) as json from study.architecturallayer), " +
                        "    topics as (select array_to_json(array_agg(t.description order by t.sequenceno)) as topics, t.module_id as module from study.moduletopic as t group by t.module_id), " +
                        "    moduleskills as (select lg.module_id as module, json_build_object('lifecycle_activity', (select (num - 1) from acrow where id = q.activity_id), 'architectural_layer', (select (num - 1) from alrow where id = q.architecturallayer_id), 'level', max(los.level)) as json from study.learninggoal as lg inner join study.learninggoal_qualification as lg2q on lg2q.learninggoal_id = lg.id inner join study.qualification as q on lg2q.qualification_id = q.id inner join study.levelofskill as los on los.id = q.levelofskill_id group by lg.module_id, q.activity_id, q.architecturallayer_id), " +
                        "    lecturers as (select array_to_json(array_agg(CONCAT(e.firstname, ' ', e.lastname))) as json, me.module_id as module from study.module_employee as me inner join study.employee as e on e.id = me.employee_id group by me.module_id), " +
                        "    grading as (select array_agg(json_build_object('subcode', ma.code, 'description', ma.description, 'percentage', coalesce(ma.weight, 0.0), 'minimal_grade', ma.minimumgrade, 'remark', coalesce(ma.remarks, '')) order by ma.code) as json, ma.module_id as module from study.moduleassessment as ma group by ma.module_id) " +
                        "select json_build_object( " +
                        "  'code', m.code, " +
                        "  'name', m.name, " +
                        "  'credits', m.credits, " +
                        "  'credentials', coalesce(md.credentials, ''), " +
                        "  'lecturers', (select json from lecturers where module = m.id), " +
                        "  'lifecycle_activities', (select json from acitivies), " +
                        "  'architectural_layers', (select json from als), " +
                        "  'learning_goals', (select json from lg where lg.module_id = m.id), " +
                        "  'lectures_in_week', m.lecturesperweek, " +
                        "  'practical_hours_week', m.practicalperweek, " +
                        "  'total_effort', m.totaleffort, " +
                        "  'introductorytext', coalesce(md.introduction, ''), " +
                        "  'additional_information', coalesce(md.additionalinfo, ''), " +
                        "  'topics', (select coalesce(topics, '[]'::JSON) from topics where module = m.id), " +
                        "  'semester', mp.semester, " +
                        "  'teaching_material', (select array_to_json(mat.descs) from material as mat where mat.md_id = md.id), " +
                        "  'prior_knowledge_references', (select coalesce(array_to_json(array_agg(prior.prior_modules)), '[]'::JSON) from prior where prior.module = m.id), " +
                        "  'qualifications', (select array_to_json(array_agg(json)) from moduleskills where module = m.id), " +
                        "  'assesment_parts', (select json from grading where module = m.id) " +
                        ") as module from study.module as m " +
                        "  left join study.moduledescription as md on md.module_id = m.id " +
                        "  left join study.module_profile as mp on mp.module_id = m.id " +
                        "  left join study.profile as p on mp.profile_id = p.id " +
                        "where m.code = ? AND p.studyprogramme_id = ?;";
    }
}
