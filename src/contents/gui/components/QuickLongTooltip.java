package contents.gui.components;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.util.Duration;

// Tooltip with altered defaults, to appear in 0.2 seconds and stay out for 60
public class QuickLongTooltip extends Tooltip {

    // Constants:
    private static final int DELAY = 100;
    private static final int DURATION = 60000;
    private static final int FONT_SIZE = 16;

    // Constructors:
    // Empty
    public QuickLongTooltip() {
        super();
        this.setShowDelay(new Duration(DELAY));
        this.setShowDuration(new Duration(DURATION));
        this.setFont(new Font(FONT_SIZE));
    }

    // With text
    public QuickLongTooltip(String text) {
        super(text);
        this.setShowDelay(new Duration(DELAY));
        this.setShowDuration(new Duration(DURATION));
        this.setFont(new Font(FONT_SIZE));
    }
}
