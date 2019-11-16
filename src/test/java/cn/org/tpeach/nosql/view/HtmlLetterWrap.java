package cn.org.tpeach.nosql.view;

import cn.org.tpeach.nosql.framework.LarkFrame;

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class HtmlLetterWrap { 
 
    public HtmlLetterWrap(){ 
        final JFrame frame = new JFrame("Letter wrap test"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
 
        final JEditorPane htmlTextPane = new JEditorPane();
        JEditorPane noHtmlTextPane = new JEditorPane();
        LarkFrame.executorService.scheduleAtFixedRate(()->{
//            Document document = Jsoup.parse(htmlTextPane.getText());
//            Elements body = document.getElementsByTag("body");
//            System.out.println(body.text());
            System.out.println(noHtmlTextPane.getText());

        },1,1, TimeUnit.SECONDS);
        htmlTextPane.setEditorKit(new HTMLEditorKit(){
           @Override 
           public ViewFactory getViewFactory(){ 
 
               return new HTMLFactory(){ 
                   public View create(Element e){ 
                      View v = super.create(e); 
                      if(v instanceof InlineView){ 
                          return new InlineView(e){ 
                              public int getBreakWeight(int axis, float pos, float len) { 
                                  return GoodBreakWeight; 
                              } 
                              public View breakView(int axis, int p0, float pos, float len) { 
                                  if(axis == View.X_AXIS) { 
                                      checkPainter(); 
                                      int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len); 
                                      if(p0 == getStartOffset() && p1 == getEndOffset()) { 
                                          return this; 
                                      } 
                                      return createFragment(p0, p1); 
                                  } 
                                  return this; 
                                } 
                            }; 
                      } 
                      else if (v instanceof ParagraphView) { 
                          return new ParagraphView(e) { 
                              protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) { 
                                  if (r == null) { 
                                        r = new SizeRequirements(); 
                                  } 
                                  float pref = layoutPool.getPreferredSpan(axis); 
                                  float min = layoutPool.getMinimumSpan(axis); 
                                  // Don't include insets, Box.getXXXSpan will include them. 
                                    r.minimum = (int)min; 
                                    r.preferred = Math.max(r.minimum, (int) pref); 
                                    r.maximum = Integer.MAX_VALUE; 
                                    r.alignment = 0.5f; 
                                  return r; 
                                } 
 
                            }; 
                        } 
                      return v; 
                    } 
                }; 
            } 
        }); 
 
        htmlTextPane.setContentType("text/html"); 
        htmlTextPane.setText("This text pane contains html. The custom HTMLEditorKit supports single letter wrapping.111");
 

        noHtmlTextPane.setText("This text pane contains no html. It supports single letter wrapping!"); 
 
        htmlTextPane.setMinimumSize(new Dimension(0, 0)); 
        noHtmlTextPane.setMinimumSize(new Dimension(0, 0)); 
 
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, noHtmlTextPane, htmlTextPane); 
        splitPane.setContinuousLayout(true); 
 
        frame.add(splitPane); 
 
        frame.setSize(200, 200); 
        frame.setVisible(true); 
        splitPane.setDividerLocation(.5); 
    } 
 
  public static void main(String[] args) { 
      new HtmlLetterWrap();
  } 
}