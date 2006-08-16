package net.sf.borg.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import net.sf.borg.common.util.PrefName;
import net.sf.borg.common.util.Prefs;
import net.sf.borg.model.Appointment;

public class ApptBoxPanel extends JPanel {

	public static final double prev_scale = 1.5; // common preview scale

	// factor

	final static private BasicStroke highlight = new BasicStroke(2.0f);

	final static private BasicStroke regular = new BasicStroke(1.0f);

	private class MyMouseListener implements MouseListener {
		public MyMouseListener() {

		}

		public void mouseClicked(MouseEvent evt) {
			// System.out.println("mouse clicked " + evt.getX() + " " +
			// evt.getY());

			// determine which box is selected, if any
			Iterator it = boxes.iterator();
			while (it.hasNext()) {

				Box b = (Box) it.next();

				// this is bad magic
				int realx = (int) ((b.x + 10) * prev_scale);
				int realy = (int) ((b.y + 10) * prev_scale);
				int realh = (int) (b.h * prev_scale);
				int realw = (int) (b.w * prev_scale);

				// System.out.println(b.layout.getAppt().getText() + " " + realx
				// + " " + realy + " " + realw + " " + realh);
				if (evt.getX() > realx && evt.getX() < (realx + realw)
						&& evt.getY() > realy && evt.getY() < (realy + realh)) {
					b.layout.setSelected(true);
					// System.out.println(true);
					if( evt.getClickCount() > 1 )
					{
						Appointment ap = b.layout.getAppt();
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(ap.getDate());
						AppointmentListView ag = new AppointmentListView(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));						
				        ag.showApp(ap.getKey());
				        ag.setVisible(true);

					}
				} else {
					b.layout.setSelected(false);
				}
			}

			evt.getComponent().getParent().repaint();
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}
	}

	private class Box {
		public int w;

		public int h;

		public int x;

		public int y;

		public ApptDayBoxLayout.ApptDayBox layout;

		public Color color;

	}

	private Collection boxes = new ArrayList();

	public ApptBoxPanel() {
		addMouseListener(new MyMouseListener());
	};

	public void addBox(ApptDayBoxLayout.ApptDayBox layout, double x, double y,
			double w, double h, Color c) {
		Box b = new Box();

		
		if( layout.isOutsideGrid())
		{
			b.x = (int)x;
			b.y = (int)y;
			b.h = (int)h;
			b.w = (int)w;
		}
		else
		{
			b.x = (int) (x + 2 + w * layout.getLeft());
			b.y = (int) (y + h * layout.getTop());
			b.h = (int) ((layout.getBottom() - layout.getTop()) * h);
			b.w = (int) ((layout.getRight() - layout.getLeft()) * w);
		}
		b.layout = layout;
		b.color = c;

		boxes.add(b);
	}

	public void drawBoxes(Graphics2D g2) {
		Shape s = g2.getClip();
		Font sm_font = g2.getFont();
		int fontDesent = g2.getFontMetrics().getDescent();
		int smfontHeight = g2.getFontMetrics().getHeight();
		Map stmap = new HashMap();
		stmap.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		stmap.put(TextAttribute.FONT, sm_font);
		boolean wrap = false;
		String sp = Prefs.getPref(PrefName.WRAP);
		if (sp.equals("true"))
			wrap = true;
		Hashtable atmap = null;
		if (wrap) {
			atmap = new Hashtable();
			atmap.put(TextAttribute.FONT, sm_font);
		}

		Stroke stroke = g2.getStroke();
		Iterator it = boxes.iterator();
		while (it.hasNext()) {
			Box b = (Box) it.next();
			Appointment ai = null;
			ai = b.layout.getAppt();

			// add a single appt text
			// if the appt falls outside the grid - leave it as
			// a note on top
			if (b.layout.isOutsideGrid()) {
				// appt is note or is outside timespan shown
				//System.out.println(b.x + " " + b.y + " " + b.w + " " +b.h);
				g2.clipRect(b.x, 0, b.w, 100);
				if (wrap) {
					String tx = ai.getText();
					AttributedString as = new AttributedString(tx, atmap);
					AttributedCharacterIterator para = as.getIterator();
					int start = para.getBeginIndex();
					int endi = para.getEndIndex();
					LineBreakMeasurer lbm = new LineBreakMeasurer(para,
							new FontRenderContext(null, false, false));
					lbm.setPosition(start);
					int tt = b.y + smfontHeight;
					while (lbm.getPosition() < endi) {
						TextLayout tlayout = lbm.nextLayout(b.w
								- (2 * fontDesent));
						tt += tlayout.getAscent();
						tlayout.draw(g2, b.x + 2, tt);
						tt += tlayout.getDescent() + tlayout.getLeading();
					}
				} else {
					if ((ai.getColor() != null && ai.getColor()
							.equals("strike"))
					/*
					 * || (ai.getTodo() && !(ai.getNextTodo() == null || !ai
					 * .getNextTodo().after(cal.getTime())))
					 */) {
						// g2.setFont(strike_font);
						// System.out.println(ai.getText());
						// need to use AttributedString to work
						// around a bug
						AttributedString as = new AttributedString(
								ai.getText(), stmap);
						g2.drawString(as.getIterator(), b.x+2, b.y + smfontHeight);
					} else {
						// g2.setFont(sm_font);
						g2.drawString(ai.getText(), b.x+2, b.y + smfontHeight);
					}
				}

				if (b.layout.isSelected()) {
					g2.setStroke(regular);
					g2.setColor(Color.BLUE);
					g2.drawRect(b.x, b.y+2, b.w, b.h);
					g2.setStroke(stroke);
					g2.setColor(Color.BLACK);
				}
			} else {

				// fill the box with color
				g2.setColor(b.color);

				g2.fillRect(b.x, b.y, b.w, b.h);

				// draw box outline

				if (b.layout.isSelected()) {
					g2.setStroke(highlight);
					g2.setColor(Color.BLUE);

				} else {
					g2.setStroke(regular);
					g2.setColor(Color.BLACK);
				}

				g2.drawRect(b.x, b.y, b.w, b.h);
				g2.setStroke(stroke);

				// draw the appt text
				g2.setColor(Color.BLACK);
				g2.clipRect(b.x, b.y, b.w, b.h);

				// add a single appt text

				// change color for a single appointment based on
				// its color - only if color print option set
				g2.setColor(Color.black);
				if (ai.getColor().equals("red"))
					g2.setColor(Color.red);
				else if (ai.getColor().equals("green"))
					g2.setColor(Color.green);
				else if (ai.getColor().equals("blue"))
					g2.setColor(Color.blue);

				if (wrap) {
					String tx = ai.getText();
					AttributedString as = new AttributedString(tx, atmap);
					AttributedCharacterIterator para = as.getIterator();
					int start = para.getBeginIndex();
					int endi = para.getEndIndex();
					LineBreakMeasurer lbm = new LineBreakMeasurer(para,
							new FontRenderContext(null, false, false));
					lbm.setPosition(start);
					while (lbm.getPosition() < endi) {
						TextLayout tlayout = lbm.nextLayout(b.w
								- (2 * fontDesent));
						b.y += tlayout.getAscent();
						tlayout.draw(g2, b.x + 2, b.y);
						b.y += tlayout.getDescent() + tlayout.getLeading();
					}
				} else {
					if ((ai.getColor() != null && ai.getColor()
							.equals("strike"))
					/*
					 * || (ai.getTodo() && !(ai .getNextTodo() == null || !ai
					 * .getNextTodo().after( cal.getTime())))
					 */) {
						// g2.setFont(strike_font);
						// System.out.println(ai.getText());
						// need to use AttributedString to work
						// around a bug
						AttributedString as = new AttributedString(
								ai.getText(), stmap);
						g2.drawString(as.getIterator(), b.x + 2, b.y
								+ smfontHeight);
					} else {
						// g2.setFont(sm_font);
						g2.drawString(ai.getText(), b.x + 2, b.y
										+ smfontHeight);
					}
				}

				

			}
			g2.setClip(s);
			g2.setColor(Color.black);
		}

		g2.setColor(Color.black);

	}

	public void clearBoxes() {
		boxes.clear();
	}

	// mouse click callback

}
