package com.ds160607.crazyinput.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPixelArray;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.TextBaseline;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class implement a non-native custom web text input control<br>
 * which flips vertically all the letter at even positions.
 * <p>
 * <b>Example 1</b>
 * 
 * <pre>
 * {@code public class Crazyinput implements EntryPoint {	
 * 	private CrazyInputWidget crInput;
 * 	public void onModuleLoad() {	
 * 		
 * 		RootPanel rootPanel = RootPanel.get();
 * 		
 * 		crInput = new CrazyInputWidget();			
 * 		
 * 		//Add some style
 * 		crInput.addStyleName("crazyInput");		
 * 		
 * 		//Put the element to a body
 * 		rootPanel.add(crInput);		
 * 	}
 * }
 * </pre>
 * 
 * *
 * <p>
 * <b>Example 2</b>
 * 
 * <pre>
 * {@code public class Crazyinput implements EntryPoint {	
 * 	private CrazyInputWidget crInput;
 * 	public void onModuleLoad() {	
 * 		
 * 		RootPanel rootPanel = RootPanel.get();
 * 		
 * 		crInput = new CrazyInputWidget(200,50); // width -200px, height -50px	
 * 		
 * 		//Add some style
 * 		crInput.addStyleName("crazyInput");		
 * 		
 * 		//Put the element to a body
 * 		rootPanel.add(crInput);		
 * 	}
 * }
 * </pre>
 * 
 * }
 * 
 * @author Dmitry Grushin
 */
public class CrazyInputWidget extends Composite {

	static final int DELAY_BLINK = 500; // cursor blinking interval in ms
	static final int MAXIMUM_INPUT_LENGTH = 310;
	static final String UNSUPPORTED_BROWSER = "Your browser does not support the HTML5 Canvas";
	static final List<String> WORD_SEPARATORS = Arrays.asList(" ", ",", ".",
			"-", "'", "\"", "?", "!", "#", "№", ":", ";", "(", ")", "=", "+",
			"-", "/", "\\"); // this array is used for a double-click text
								// selection

	// Lower case symbols
	static final List<String> LOWERCASE = Arrays.asList("a", "b", "c", "d",
			"e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
			"r", "s", "t", "u", "v", "w", "x", "y", "z", "а", "б", "в", "г",
			"д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п",
			"р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь",
			"э", "ю", "я");

	// Corrections of y-coordinate for vertically flipped symbols
	private double numHeightCorrection = 0; // for no lower case symbols
	private double xHeightCorrection = 0; // for lower case symbols

	private Canvas canvas;
	private Canvas backBuffer;

	private Context2d context;
	private Context2d backBufferContext;
	private ImageData cursorData;

	private int width = 100; //default width of the element (px)
	private int height = 20; //default height of the element (px)

	private ArrayList<Letter> letters = new ArrayList<Letter>();

	private int cursorPosition = -1;
	private double cursorPositionX = 0;
	private boolean cursorBlink = false;
	private Timer blinkTimer;

	private boolean hasFocus = false;

	private boolean isSelection = false;
	private int startSelectPosition = 0;
	private int endSelectPosition = 0;

	//Default styles
	private String fontColorNormal = "#000000";
	private String fontColorInverted = "#FFFFFF";
	private String backgroundColor = "#FFFFFF";
	private String selectionColor = "#AAAAAA";
	private String fontStyle = "20pt arial";

	private static CrazyInputWidgetUiBinder uiBinder = GWT
			.create(CrazyInputWidgetUiBinder.class);

	interface CrazyInputWidgetUiBinder extends
			UiBinder<Widget, CrazyInputWidget> {
	}

	@UiField
	FlowPanel fDiv;

	@UiField
	BifacialTextBox curtain;

	@UiField
	WrapedCanvas wraped_canvas;
	private boolean isMobile = false;

	// private Logger logger;

	private void init() {
		// logger = Logger.getLogger(">");
		// logger.log(Level.INFO, Navigator.getPlatform());

		if ((!(Navigator.getPlatform().equals("HP-UX")))
				&& (!(Navigator.getPlatform().equals("Linux i686")))
				&& (!(Navigator.getPlatform().equals("Mac68K")))
				&& (!(Navigator.getPlatform().equals("MacPPC")))
				&& (!(Navigator.getPlatform().equals("MacIntel")))
				&& (!(Navigator.getPlatform().equals("Win16")))
				&& (!(Navigator.getPlatform().equals("Win32")))) {
			isMobile = true;
		}
		
		letters = new ArrayList<Letter>();

		backBuffer = Canvas.createIfSupported();
		if (backBuffer == null) {
			initWidget(new Label(UNSUPPORTED_BROWSER));
			return;
		}

		initWidget(uiBinder.createAndBindUi(this));

		canvas = wraped_canvas.getCanvas();
		context = canvas.getContext2d();
		backBufferContext = backBuffer.getContext2d();

		applySize();
		applyStyle();

		initHandlers();

		blinkTimer = new Timer() {
			@Override
			public void run() {
				if (checkSize() || checkStyle()) {
					invalidate();
				} else {
					cursorBlink = !cursorBlink;
					backBufferContext.beginPath();
					drawCursor();
					backBufferContext.closePath();
					backBufferContext.stroke();

					context.drawImage(backBufferContext.getCanvas(), 0, 0);
				}
				blinkTimer.schedule(DELAY_BLINK);
			}
		};
		blinkTimer.schedule(DELAY_BLINK);
	}

	/**
	 * Create a CrazyInputWidget element *
	 * 
	 * @wbp.parser.constructor
	 */
	public CrazyInputWidget() {
		init();
	}

	/**
	 * Create a CrazyInputWidget element
	 * 
	 * @param width
	 *            a width of an input in pixels
	 * @param height
	 *            a height of an input in pixels
	 */
	public CrazyInputWidget(int width, int height) {
		setWidth(width);
		setHeight(height);
		init();
	}

	/**
	 * Set the value of this Widget
	 * 
	 * @param s
	 *            String
	 */
	public void setValue(String s) {
		// TO-DO
	}

	/**
	 * Get the value of an input element
	 * 
	 * @return String
	 */
	public String getValue(String s) {
		String res = "";
		for (int i = 0; i < this.letters.size(); i++) {
			res += this.letters.get(i).getChar();
		}
		return res;
	}

	/**
	 * Add letters at the cursor position. And move cursor to the end of this
	 * letters
	 * 
	 * @param s
	 *            String
	 */
	private void addLetters(String str) {

		if (this.startSelectPosition != this.endSelectPosition) {
			removeSelected();
		}
		for (int i = 0; (i < str.length())
				&& (this.letters.size() < MAXIMUM_INPUT_LENGTH); i++) {
			double w = getTextWidth(str.substring(i, i + 1));
			this.cursorPosition++;
			this.cursorPositionX += w;
			this.letters.add(this.cursorPosition,
					new Letter(str.substring(i, i + 1), w));

		}

		this.startSelectPosition = this.cursorPosition;
		this.endSelectPosition = this.cursorPosition;
		// invalidate();

	}
	/**
	 * Get a width (px) of given string
	 * @param s String
	 * @return double
	 */
	private double getTextWidth(String s) {
		double w;
		w = backBufferContext.measureText(s).getWidth();
		return w;
	}

	/**
	 * Calculate whole width of the inputed text
	 * 
	 * @return double width in pixel
	 */
	private double wholeLength() {
		double r = 0;
		for (int i = 0; i < this.letters.size(); i++) {
			r += this.letters.get(i).getWidth();
		}
		return r;
	}

	/**
	 * Calculate a width of a given range
	 * 
	 * @param start int - not included
	 * @param end int - included
	 * @return double width in pixel
	 */
	private double getLengthOfRange(int start, int end) {
		double r = 0;
		int s = start;
		int e = end;
		if (s > e) {
			s = end;
			e = start;
		}
		if (s < 0) {
			s = 0;
		}
		if (s < this.letters.size()) {
			for (int i = s; (i < this.letters.size()) && (i <= e); i++) {
				r += this.letters.get(i).getWidth();
			}
		}
		return r;
	}

	private double wholeLengthAfterCursor() {
		double r = 0;
		for (int i = this.cursorPosition + 1; i < this.letters.size(); i++) {
			r += this.letters.get(i).getWidth();
		}
		return r;
	}

	private double wholeLengthBeforeCursor() {
		double r = 0;
		for (int i = 0; (i < this.letters.size()) && (i <= this.cursorPosition); i++) {
			r += this.letters.get(i).getWidth();
		}
		return r;
	}

	/**
	 * Remove a selected text
	 */
	private void removeSelected() {
		if (this.letters.size() != 0) {
			int ss = 0;
			int se = 0;
			if (this.startSelectPosition < this.endSelectPosition) {
				ss = this.startSelectPosition + 1;
				se = this.endSelectPosition;
				this.cursorPositionX -= getLengthOfRange(ss, se);
			} else {
				se = this.startSelectPosition;
				ss = this.endSelectPosition + 1;
			}

			this.cursorPosition = ss - 1;
			this.startSelectPosition = this.cursorPosition;
			this.endSelectPosition = this.cursorPosition;
			this.letters.subList(ss, se + 1).clear();

			double wbc = wholeLengthBeforeCursor();
			double wac = wholeLengthAfterCursor();
			double wl = wholeLength();

			if (wl < this.width) {
				this.cursorPositionX = wbc;
			} else {
				if (wac < (this.width - this.cursorPositionX)) {
					this.cursorPositionX = this.width - wac;
				}
			}
		}
	}

	/**
	 * Remove a letter before a cursor
	 */
	private void removeLetter() {
		if (this.startSelectPosition != this.endSelectPosition) {
			removeSelected();
			invalidate();
		} else {
			if ((this.cursorPosition != -1) && (this.letters.size() != 0)) {

				double w = this.letters.get(this.cursorPosition).getWidth();
				this.letters.remove(this.cursorPosition);

				this.cursorPosition--;

				double wac = wholeLengthAfterCursor();
				double wbc = wholeLengthBeforeCursor();

				if (wholeLength() > this.width) {

					if (wac > this.width - this.cursorPositionX) {
						this.cursorPositionX -= w;
					} else {
						this.cursorPositionX = this.width - wac;
					}
				} else {
					this.cursorPositionX = wbc;
				}

				if ((this.cursorPositionX <= 0) && (wbc > 0)) {
					if (wbc < this.width / 2) {
						this.cursorPositionX = wbc;
					} else {
						this.cursorPositionX = this.width / 2;
					}
				}

				invalidate();
			}
		}
	}

	/**
	 * remove a letter after a cursor
	 */
	private void deleteLetter() {
		if (this.startSelectPosition != this.endSelectPosition) {
			removeSelected();
			invalidate();
		} else {
			if (this.cursorPosition < this.letters.size() - 1) {
				this.letters.remove(this.cursorPosition + 1);
				double wac = wholeLengthAfterCursor();
				double wl = wholeLength();

				if (wl > this.width) {
					if (wac - (this.width - this.cursorPositionX) < 0) {
						this.cursorPositionX = this.width - wac;
					}
				}
				invalidate();
			}
		}
	}

	/**
	 * Move cursor at the begin of text
	 */
	private void moveHome(boolean shift) {
		this.cursorPosition = -1;
		if (!shift) {
			this.startSelectPosition = this.cursorPosition;
			this.endSelectPosition = this.cursorPosition;
		} else {
			this.endSelectPosition = this.cursorPosition;
		}
		this.cursorPositionX = 0;
		invalidate();
	}

	/**
	 * Move cursor at the end of text
	 */
	private void moveEnd(boolean shift) {

		this.cursorPosition = this.letters.size() - 1;

		if (!shift) {
			this.startSelectPosition = this.cursorPosition;
			this.endSelectPosition = this.cursorPosition;
		} else {
			this.endSelectPosition = this.cursorPosition;
		}

		double w = wholeLength();
		if (w > this.width) {
			this.cursorPositionX = this.width;
		} else {
			this.cursorPositionX = w;
		}
		invalidate();
	}

	/**
	 * Set a width of this widget
	 * 
	 * @param width
	 *            in pixels
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Set a height of this widget
	 * 
	 * @param height
	 *            in pixels
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Apply a current width and height to a canvas
	 */
	private void applySize() {
		canvas.setCoordinateSpaceWidth(this.width);
		canvas.setCoordinateSpaceHeight(this.height);
		backBuffer.setCoordinateSpaceWidth(this.width);
		backBuffer.setCoordinateSpaceHeight(this.height);
	}

	/**
	 * Check the width and the height and apply it to a canvas element if
	 * changed
	 * 
	 * @return boolean true - if something was changed
	 */
	private boolean checkSize() {
		int w = wraped_canvas.getElement().getClientWidth();
		int h = wraped_canvas.getElement().getClientHeight();
		if ((this.width != w) || (this.height != h)) {
			this.width = w;
			this.height = h;
			applySize();
			applyStyle();
			return true;
		}
		return false;
	}

	/**
	 * Apply current font style to a canvas element;
	 */
	private void applyStyle() {
		backBufferContext.setFont(fontStyle);
		for (int i = 0; i < this.letters.size(); i++) {
			this.letters.get(i).setWidth(
					getTextWidth(this.letters.get(i).getChar()));
		}
		xHeightCorrection = getTextHeight(backBufferContext, fontStyle, "w");
		numHeightCorrection = getTextHeight(backBufferContext, fontStyle, "[");
	}

	/**
	 * Check current font style and apply it to a canvas element if changed
	 * 
	 * @return boolean true - if something was changed
	 */
	private boolean checkStyle() {

		String cl = getComputedStyleProperty(fDiv.getElement(), "color");
		String bcl = getComputedStyleProperty(fDiv.getElement(),
				"background-color");
		String fs = getComputedStyleProperty(fDiv.getElement(), "font-size")
				+ " "
				+ getComputedStyleProperty(fDiv.getElement(), "font-family");
		// getComputedStyleProperty(fDiv.getElement(), "font-weight"); //work only in Chrome
		// getComputedStyleProperty(fDiv.getElement(), "font-style") + " " + //work only in Chrome

		if (bcl.equals("rgba(0, 0, 0, 0)")) {
			bcl = "#FFFFFF";
		}

		if (!(fs.equals(fontStyle)) || (!(fontColorNormal.equals(cl)))
				|| (!(backgroundColor.equals(bcl)))) {

			// logger.log(Level.INFO, "FONT: " + fs);

			fontStyle = fs;
			backgroundColor = bcl;
			fontColorNormal = cl;

			applySize();
			applyStyle();

			this.cursorPosition = -1;
			this.startSelectPosition = -1;
			this.endSelectPosition = -1;
			this.cursorPositionX = 0;
			return true;
		}
		return false;
	}

	private void drawCursor() {
		if ((this.startSelectPosition == this.endSelectPosition)
				&& (this.hasFocus)) {
			double _x = this.cursorPositionX + 0.5;
			if (this.cursorPosition == -1) {
				this.cursorPositionX = 0;
				_x = 1.5;
			}
			if (_x > this.width) {
				_x = this.width - 1.5;
			}
			if (cursorBlink) {
				if (cursorData != null) {
					backBufferContext.putImageData(cursorData, _x - 2, 0);
				}
			} else {
				cursorData = backBufferContext.getImageData(_x - 2, 0, 6,
						this.height);
				backBufferContext.setStrokeStyle(fontColorNormal);
				backBufferContext.moveTo(_x, 2.5);
				backBufferContext.lineTo(_x, this.height - 2.5);
			}
		}
	}

	/**
	 * Force this widget to repaint
	 */
	private void invalidate() {

		blinkTimer.cancel();

		checkSize();
		checkStyle();

		if (this.cursorPositionX < 0) {
			this.cursorPositionX = 1;
		}
		if (this.cursorPositionX > this.width) {
			this.cursorPositionX = this.width - 1;
		}

		int ss = 0;
		int se = 0;
		if (this.startSelectPosition < this.endSelectPosition) {
			ss = this.startSelectPosition;
			se = this.endSelectPosition;
		} else {
			se = this.startSelectPosition;
			ss = this.endSelectPosition;
		}

		// synchronize the selection with the curtain
		if (!isMobile) {
			if (se != ss) {
				String s = "";
				for (int i = ss + 1; i <= se; i++) {
					s += this.letters.get(i).getChar();
				}
				curtain.setValue(s);
				curtain.setSelectionRange(0, s.length());
			} else {
				curtain.setValue("");
			}
		}

		backBufferContext.beginPath();
		backBufferContext.setFillStyle(backgroundColor);
		backBufferContext.setFont(fontStyle);
		backBufferContext.fillRect(0, 0, this.width, this.height);

		//Draw a text before the cursor
		if (this.cursorPositionX > 0) {
			double x = this.cursorPositionX;
			for (int i = this.cursorPosition; i > -1; i--) {
				Letter l = this.letters.get(i);
				boolean even = ((i + 1) % 2 == 0);
				x -= l.getWidth();

				// draw selection
				if ((i > ss) && (i <= se)) {
					backBufferContext.setFillStyle(selectionColor);
					backBufferContext.fillRect(x, 0, l.getWidth() + 0.5,
							this.height);
					backBufferContext.setFillStyle(fontColorInverted);
				} else {
					backBufferContext.setFillStyle(fontColorNormal);
				}

				if (even) {

					backBufferContext.save();
					if (LOWERCASE.indexOf(l.getChar()) == -1) {
						backBufferContext.translate(x, numHeightCorrection);
					} else {
						backBufferContext.translate(x, xHeightCorrection);
					}
					backBufferContext.scale(1, -1);

					backBufferContext.setTextBaseline(TextBaseline.ALPHABETIC);
					backBufferContext.fillText(l.getChar(), 0.5, 0);
					backBufferContext.restore();

				} else {
					backBufferContext.setTextBaseline(TextBaseline.TOP);
					backBufferContext.fillText(l.getChar(), x + 0.5, 0);
				}

			}
		}

		//Draw a text after the cursor
		if (this.cursorPositionX < this.width) {
			double x = this.cursorPositionX;
			for (int i = this.cursorPosition + 1; i < this.letters.size(); i++) {
				Letter l = this.letters.get(i);
				boolean even = ((i + 1) % 2 == 0);

				// draw selection
				if ((i > ss) && (i <= se)) {
					backBufferContext.setFillStyle(selectionColor);
					backBufferContext.fillRect(x, 0, l.getWidth() + 0.5,
							this.height);
					backBufferContext.setFillStyle(fontColorInverted);
				} else {
					backBufferContext.setFillStyle(fontColorNormal);
				}
				if (even) {
					backBufferContext.save();
					if (LOWERCASE.indexOf(l.getChar()) == -1) {
						backBufferContext.translate(x, numHeightCorrection);
					} else {
						backBufferContext.translate(x, xHeightCorrection);
					}
					backBufferContext.scale(1, -1);
					backBufferContext.setTextBaseline(TextBaseline.ALPHABETIC);
					backBufferContext.fillText(l.getChar(), 0.5, 0);
					backBufferContext.restore();

				} else {
					backBufferContext.setTextBaseline(TextBaseline.TOP);
					backBufferContext.fillText(l.getChar(), x + 0.5, 0);
				}

				x += l.getWidth();

			}

		}

		cursorBlink = false;
		cursorData = null;
		drawCursor();
		blinkTimer.schedule(DELAY_BLINK);

		backBufferContext.closePath();
		backBufferContext.stroke();

		context.drawImage(backBufferContext.getCanvas(), 0, 0);
	}

	/**
	 * Set a cursor to a letter which is under X (px) coordinate
	 * 
	 * @param x
	 */
	private void setCursorTo(int x, boolean shift) {
		double offset = wholeLengthBeforeCursor() - this.cursorPositionX;
		double rx = x + offset;
		double ix = 0;
		int j = -2;
		for (int i = -1; i < this.letters.size(); i++) {
			if (i > -1) {
				double w = this.letters.get(i).getWidth();
				if (ix + w / 2 > rx) {
					j = i - 1;
					break;
				}
				ix += w;
			}
			if (ix > rx) {
				j = i;
				break;
			}
		}
		double wl = wholeLength();
		if (rx > wl) {
			j = this.letters.size() - 1;
			ix = wl + offset;
		}

		if (j != -2) {
			this.cursorPosition = j;
			if (!shift) {
				this.startSelectPosition = this.cursorPosition;
				this.endSelectPosition = this.cursorPosition;
			} else {
				this.endSelectPosition = this.cursorPosition;
			}
			this.cursorPositionX = ix - offset;
			invalidate();
		}
	}

	private void doBlur() {
		this.hasFocus = false;
		this.startSelectPosition = this.cursorPosition;
		this.endSelectPosition = this.cursorPosition;
		invalidate();
	}

	private void doFocus() {
		blinkTimer.cancel();
		this.hasFocus = true;
		cursorBlink = false;
		backBufferContext.beginPath();
		drawCursor();
		backBufferContext.closePath();
		backBufferContext.stroke();
		context.drawImage(backBufferContext.getCanvas(), 0, 0);
		blinkTimer.schedule(DELAY_BLINK);
	}

	private void chooseword() {
		int s = -1;
		int e = this.letters.size();

		if (this.cursorPosition != -1) {
			for (int i = this.cursorPosition; i > -1; i--) {
				if (WORD_SEPARATORS.indexOf(this.letters.get(i).getChar()) != -1) {
					s = i;
					break;
				}
			}
		}

		for (int i = this.cursorPosition; i < this.letters.size(); i++) {
			if (i > -1) {
				if (WORD_SEPARATORS.indexOf(this.letters.get(i).getChar()) != -1) {
					e = i;
					break;
				}
			}
		}

		this.startSelectPosition = s;
		this.endSelectPosition = e - 1;
		invalidate();
	}

	private void initHandlers() {

		// focus
		curtain.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				doBlur();
			}
		});
		curtain.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				doFocus();
			}
		});

		// mouse
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
				case Event.ONMOUSEUP:
					isSelection = false;
					break;
				case Event.ONMOUSEMOVE:
					if (isSelection) {
						event.getNativeEvent().preventDefault();
						int mx = event.getNativeEvent().getClientX();
						mx -= canvas.getAbsoluteLeft();
						setCursorTo(mx, true);
					}
					break;
				}
			}
		});
		curtain.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				chooseword();
			}
		});
		curtain.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (event.getNativeButton() == 1) {
					event.stopPropagation();
					int mx = event.getRelativeX(canvas.getElement());
					isSelection = true;
					setCursorTo(mx, event.isShiftKeyDown());
				}
			}
		});

		// keyboard
		if (!isMobile) {
			curtain.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if ((!event.isAltKeyDown()) && (!event.isControlKeyDown())) {
						if (event.getUnicodeCharCode() != 0) {
							event.preventDefault();
							event.stopPropagation();
							String s = Character.toString((char) event
									.getUnicodeCharCode());
							addLetters(s);
							invalidate();
						}
					}
				}
			});
		}

		curtain.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				boolean shift = event.isShiftKeyDown();
				boolean ctrl = event.isControlKeyDown();

				// logger.log(Level.INFO, "key down: " +
				// event.getNativeKeyCode());

				switch (event.getNativeKeyCode()) {
				case 65:// CRTL+A
					if (ctrl) {
						selectAll();
					}
					break;
				case KeyCodes.KEY_LEFT:
					event.preventDefault();
					moveCursorLeft(shift);
					break;
				case KeyCodes.KEY_RIGHT:
					event.preventDefault();
					moveCursorRight(shift);
					break;
				case KeyCodes.KEY_BACKSPACE:
					event.preventDefault();
					removeLetter();
					break;
				case KeyCodes.KEY_DELETE:
					event.preventDefault();
					deleteLetter();
					break;
				case KeyCodes.KEY_HOME:
					event.preventDefault();
					moveHome(shift);
					break;
				case KeyCodes.KEY_END:
					event.preventDefault();
					moveEnd(shift);
					break;
				}

			}
		});
		if (isMobile) {
			addInputHandler(curtain.getElement());
		}
		addPasteHandler(curtain.getElement());
		addCutHandler(curtain.getElement());

	}

	// INPUT
	public native void addInputHandler(Element element)
	/*-{
		var temp = this; // hack to hold on to 'this' reference
		element.oninput = function(e) {
			var _e = e;
			setTimeout(
					function() {
						temp.@com.ds160607.crazyinput.client.CrazyInputWidget::proceedInput()()
					}, 1);
		}
	}-*/;

	private void proceedInput() {
		addLetters(curtain.getValue());
		curtain.setValue("");
		invalidate();
	}

	// CUT
	public native void addCutHandler(Element element)
	/*-{
		var temp = this; // hack to hold on to 'this' reference
		element.oncut = function(e) {
			setTimeout(
					function() {
						temp.@com.ds160607.crazyinput.client.CrazyInputWidget::proceedCut()()
					}, 1);
		}
	}-*/;

	private void proceedCut() {
		removeSelected();
		invalidate();
	}

	// PASTE
	public native void addPasteHandler(Element element)
	/*-{
		var temp = this; // hack to hold on to 'this' reference
		element.onpaste = function(e) {
			setTimeout(
					function() {
						temp.@com.ds160607.crazyinput.client.CrazyInputWidget::proceedPaste()()
					}, 1);
		}
	}-*/;

	private void proceedPaste() {
		String s = curtain.getValue();
		if (this.startSelectPosition != this.endSelectPosition) {
			removeSelected();
		}
		curtain.setValue("");
		addLetters(s);
		invalidate();
	}

	private void moveCursorLeft(boolean shift) {
		if (!shift) {
			int s = this.startSelectPosition;
			int e = this.endSelectPosition;
			int c = this.cursorPosition;
			if (s != e) {
				if (s < e) {
					this.cursorPosition = s;
				} else {
					this.cursorPosition = e;
				}

				if (this.cursorPosition > c) {
					this.cursorPositionX += getLengthOfRange(c + 1,
							this.cursorPosition);
				}
				if (this.cursorPosition < c) {
					this.cursorPositionX -= getLengthOfRange(
							this.cursorPosition + 1, c);
				}

				this.startSelectPosition = this.cursorPosition;
				this.endSelectPosition = this.cursorPosition;
				invalidate();
				return;
			}
		}

		if (this.cursorPosition >= 0) {
			this.cursorPositionX -= this.letters.get(this.cursorPosition)
					.getWidth();
			this.cursorPosition--;

			if (!shift) {
				this.startSelectPosition = this.cursorPosition;
				this.endSelectPosition = this.cursorPosition;
			} else {
				this.endSelectPosition = this.cursorPosition;
			}
			invalidate();
		}
	}

	private void moveCursorRight(boolean shift) {
		if (!shift) {
			int s = this.startSelectPosition;
			int e = this.endSelectPosition;
			int c = this.cursorPosition;
			if (s != e) {
				if (s > e) {
					this.cursorPosition = s;
				} else {
					this.cursorPosition = e;
				}

				if (this.cursorPosition > c) {
					this.cursorPositionX += getLengthOfRange(c + 1,
							this.cursorPosition);
				}
				if (this.cursorPosition < c) {
					this.cursorPositionX -= getLengthOfRange(
							this.cursorPosition + 1, c);
				}

				this.startSelectPosition = this.cursorPosition;
				this.endSelectPosition = this.cursorPosition;
				invalidate();
				return;
			}
		}
		if (this.cursorPosition < this.letters.size() - 1) {

			this.cursorPosition++;
			this.cursorPositionX += this.letters.get(this.cursorPosition)
					.getWidth();

			if (!shift) {
				this.startSelectPosition = this.cursorPosition;
				this.endSelectPosition = this.cursorPosition;
			} else {
				this.endSelectPosition = this.cursorPosition;
			}
			invalidate();
		}
	}

	private void selectAll() {
		this.startSelectPosition = -1;
		this.endSelectPosition = this.letters.size() - 1;
		this.cursorPosition = -1;
		this.cursorPositionX = 0;
		invalidate();
	}

	private static native void consoleLog(String s)/*-{
		console.log(s);
	}-*/;

	public static native String getComputedStyleProperty(Element element,
			String property) /*-{
		if ($doc.defaultView && $doc.defaultView.getComputedStyle) {
			return $doc.defaultView.getComputedStyle(element, null)
					.getPropertyValue(property);
		}
		return "";
	}-*/;

	public double getTextHeight(Context2d cntx, String fStyle, String s) {
		if (this.height > 0) {
			cntx.beginPath();
			cntx.setFillStyle("#FFFFFF");
			cntx.fillRect(0, 0, this.width, this.height);

			cntx.setFont(fStyle);
			cntx.setFillStyle("#000000");
			cntx.setTextBaseline(TextBaseline.TOP);
			cntx.fillText(s, 0, 0);
			cntx.closePath();
			cntx.stroke();

			CanvasPixelArray data = backBufferContext.getImageData(0, 0,
					this.width, this.height).getData();

			int stp = this.width * 4;
			int r = 255;
			int j = 0;
			for (int k = 0; k < this.width; k++) { // width
				for (j = 0; j < this.height / 2; j++) { // height
					r = data.get(j * stp + k * 4);
					if (r != 255) {
						break;
					}
				}
				if (r != 255) {
					break;
				}
			}
			return j;
		}
		return 0;
	}
}
