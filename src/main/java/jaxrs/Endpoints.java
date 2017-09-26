package jaxrs;

class Endpoints {
    private Endpoints(){
    }

    /**
     * The Curriculum endpoint.
     * The parameter describes the curriculum.
     */
    static final String CURRICULUM = "curriculum/{1}";

    /**
     * The Semesters endpoint.
     * Returns all semesters and their information in a curriculum.
     */
    static final String SEMESTERS = CURRICULUM + "/semesters";
}
