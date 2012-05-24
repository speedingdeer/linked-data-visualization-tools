package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.i18n.client.TimeZone;

/**
 * @author Daniel Garijo (based on the work by Alexander de Leon)
 */
public class SimileTimeLineEvent implements Serializable {

  private Date m_start;
  private Date m_latestStart;
  private Date m_earliestEnd;
  private Date m_end;
  private boolean m_isDuration;
  private String m_title;
  private String m_image;
  private String m_link;
  private String m_icon;
  private String m_description;
  private String m_color;
  private String m_textColor;

  public SimileTimeLineEvent(){
      }
  public SimileTimeLineEvent(Date start, Date end, String title, String description)
  {
    setStart(start);
    setEnd(end); // by default all events are instantaneous.
    setTitle(title);
    setDescription(description);
  }

  public Date getStart()
  {
    return m_start;
  }

  public void setStart(Date start)
  {
    m_start = start;
  }

  /**
   * Date format: aaaammdd: "20100603"
   * @param start Date
   */
  public void setStart(String start){
    String anyo,mes,dia;
    anyo = start.substring(0,4);
    mes = start.substring(4,6);
    dia = start.substring(6,8);
    this.setStart(new Date(Integer.parseInt(anyo)-1900, Integer.parseInt(mes), Integer.parseInt(dia), 12, 0));
  }

  public Date getEnd()
  {
    return m_end;
  }

  public void setEnd(Date end)
  {
    this.m_end = end;
  }

  /**
   * Date format: aaaammdd: "20100603"
   * @param end Date
   */
  public void setEnd(String end){
    String anyo,mes,dia;
    anyo = end.substring(0,4);
    mes = end.substring(4,6);
    dia = end.substring(6,8);
    TimeZone gmtTime = TimeZone.createTimeZone("GMT");
    Date date = new Date(Integer.parseInt(anyo)-1900, Integer.parseInt(mes), Integer.parseInt(dia), 12, 0);
    String a = date.toGMTString();
    this.setStart(new Date(Integer.parseInt(anyo)-1900, Integer.parseInt(mes), Integer.parseInt(dia), 12, 0));
  }

  public boolean isDuration()
  {
    return m_isDuration;
  }

  public void setIsDuration(boolean isDuration)
  {
    m_isDuration = isDuration;
  }

  public String getTitle()
  {
    return m_title;
  }

  public void setTitle(String title)
  {
    m_title = title;
  }

  public String getImage()
  {
    return m_image;
  }

  public void setImage(String image)
  {
    this.m_image = image;
  }

  public String getIcon()
  {
    return m_icon;
  }

  public void setIcon(String icon)
  {
    this.m_icon = icon;
  }

  public String getDescription()
  {
    return m_description;
  }

  public void setDescription(String description)
  {
    this.m_description = description;
  }

  /**
   * Addition for descriptions: each of the features is added in
   * an unordered list
   * @param description short description
   * @param features set of reatures to display in an unordered list
   */
  public void setDescription(String description, ArrayList<String> features){
      String d = description;
      d+="&lt;ul&gt;";
      for(int i = 0; i<features.size();i++){
          d+="&lt;li&gt;"+features.get(i)+"&lt;/li&gt;";
      }
      d+="&lt;/ol&gt;";
      this.setDescription(d);
  }

  public Date getLatestStart()
  {
    return m_latestStart;
  }

  public void setLatestStart(Date latestStart)
  {
    this.m_latestStart = latestStart;
  }

  public Date getEarliestEnd()
  {
    return m_earliestEnd;
  }

  public void setEarliestEnd(Date earliestEnd)
  {
    this.m_earliestEnd = earliestEnd;
  }

  public String getLink()
  {
    return m_link;
  }

  public void setLink(String link)
  {
    this.m_link = link;
  }

  public String getColor()
  {
    return m_color;
  }

  public void setColor(String color)
  {
    this.m_color = color;
  }

  public String getTextColor()
  {
    return m_textColor;
  }

  public void setTextColor(String textColor)
  {
    this.m_textColor = textColor;
  }

  public String toXml()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<event ");
    buffer.append("start=\"" + m_start.toGMTString() + "\" ");
    if (m_end != null)
    {
      buffer.append("end=\"" + m_end.toGMTString() + "\" ");
    }
    if (m_latestStart != null)
    {
      buffer.append("latestStart=\"" + m_latestStart.toGMTString() + "\" ");
    }
    if (m_earliestEnd != null)
    {
      buffer.append("earliestEnd=\"" + m_earliestEnd.toGMTString() + "\" ");
    }
    buffer.append("isDuration=\"" + isDuration() + "\" ");
    buffer.append("title=\"" + m_title + "\" ");
    if (m_link != null)
    {
      buffer.append("link=\"" + m_link + "\" ");
    }
    if (m_icon != null)
    {
      buffer.append("icon=\"" + m_icon + "\" ");
    }
    if (m_color != null)
    {
      buffer.append("color=\"" + m_color + "\" ");
    }
    if (m_textColor != null)
    {
      buffer.append("image=\"" + m_textColor + "\" ");
    }

    buffer.append(">");
    if (m_description != null)
    {
      buffer.append(m_description);
    }
    buffer.append("</event>");

    return buffer.toString();
  }

}
