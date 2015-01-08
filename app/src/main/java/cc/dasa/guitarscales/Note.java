package cc.dasa.guitarscales;

public enum Note {
    A("A"),
    A_SHARP("A#"),
    B("B"),
    C("C"),
    C_SHARP("C#"),
    D("D"),
    D_SHARP("D#"),
    E("E"),
    F("F"),
    F_SHARP("F#"),
    G("G"),
    G_SHARP("G#");

    private String mName;

    Note(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public Note Step(int steps) {
        int n = ordinal() + (steps % 12);
        if (n > 11) {
            n %= 12;
        }
        if (n < 0) {
            n += 12;
        }
        return values()[n];
    }

    public static Note fromName(String name) {
        for (Note note : Note.values()) {
            if (note.getName().equals(name)) {
                return note;
            }
        }
        throw new RuntimeException("Unknown value: " + name);
    }
}
