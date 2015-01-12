package cc.dasa.guitarscales;

import android.test.AndroidTestCase;

public class GuitarViewTest extends AndroidTestCase {
    public void testSelections() {
        GuitarView gv = new GuitarView(getContext());
        gv.addSelections(Note.A, Note.B);
        assertEquals("number of selections", 2, gv.getSelections().size());
        assertEquals("first selection", Note.A, gv.getSelections().get(0));
        assertEquals("second selection", Note.B, gv.getSelections().get(1));
    }
}
