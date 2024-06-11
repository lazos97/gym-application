package main.helpers;
import java.time.LocalDate;

public class GetProgram {
    private final String id;
    private final String program;
    private final LocalDate date;
    private final String schedule;

    public GetProgram(String id, String program, LocalDate date, String schedule) {
        this.id = id;
        this.program = program;
        this.date = date;
        this.schedule = schedule;
    }

    public String getId() {
        return id;
    }

    public String getProgram() {
        return program;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSchedule() {
        return schedule;
    }
}