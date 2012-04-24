package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Daniel Garijo (based on the work by Alexander de Leon)
 */
public class SimileTimeLineEventContainer implements Serializable {

    //to do: this class has an event list and has to do the To String method
    //to have accesible al the data from the events.

    private ArrayList<SimileTimeLineEvent> m_list;

    private String m_dateFormat;

  public SimileTimeLineEventContainer(){
     m_list = new ArrayList<SimileTimeLineEvent>();
  }

  public SimileTimeLineEventContainer(String dateFormat)
  {
    this();
    m_dateFormat = dateFormat;
  }

  public String getDateFormat()
  {
    return m_dateFormat;
  }

  public void setDateFormat(String format)
  {
    m_dateFormat = format;
  }

  public void addEvent(SimileTimeLineEvent e)
  {
    m_list.add(e);
  }

  public Date getDateToCenter(){
      if(m_list.size()<=0)return null;
      return m_list.get(0).getStart();
  }

  public String toXml()
  {
    if(this.m_list.isEmpty())return "";
    else{
        StringBuilder buffer = new StringBuilder();
        buffer.append("<data ");
        if (m_dateFormat != null){
            buffer.append("date-time-format=\"" + m_dateFormat + "\" ");
        }
        buffer.append(">");
        for (int i = 0; i<m_list.size();i++){
            SimileTimeLineEvent event = m_list.get(i);
            buffer.append(event.toXml());
        }
        buffer.append("</data>");
        return buffer.toString();
      }
  }

}
