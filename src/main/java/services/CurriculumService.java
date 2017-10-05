package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.Connection;
import resources.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class CurriculumService {

    private final Connection conn;

    @Inject
    public CurriculumService(Connection conn){
        this.conn = conn;
    }

    public ObjectNode getCurriculumSemesters(String curriculumId) throws SQLException, IOException {
        String query =
                "SELECT coalesce(array_to_json(array_agg(row_to_json(co))), '[]'::json) as semesters\n" +
                        "FROM study.curriculum_overview co\n" +
                        "WHERE study_programme = ?;";
        final ResultSet resultSet = conn.executeQuery(query, curriculumId);
        resultSet.next();
        final String JSONString = resultSet.getString("semesters");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        ArrayNode resultSemesterArray = mapper.createArrayNode();
        result.set("semesters", resultSemesterArray);

        ArrayNode actualObj = (ArrayNode) mapper.readTree(JSONString);

        final Iterator<JsonNode> jsonIterator = actualObj.iterator();
        while(jsonIterator.hasNext()){
            ObjectNode module = (ObjectNode) jsonIterator.next();
            final int semester = module.get("semester").asInt();
            final ObjectNode currentSemester = mapper.createObjectNode();
            currentSemester.put("semester", semester);
            final ArrayNode modules = mapper.createArrayNode();
            currentSemester.set("modules", modules);

            while(module.get("semester").asInt() == semester){
                module.remove("semester");
                module.remove("name");
                module.remove("study_programme");

                modules.add(module);

                if(jsonIterator.hasNext()) module = (ObjectNode) jsonIterator.next();
            }
            modules.add(module);
            resultSemesterArray.add(currentSemester);
        }
        return result;
    }
}
