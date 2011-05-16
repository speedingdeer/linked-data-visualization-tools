<%@ page import="es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration"%>
<%@ page import="es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterNames"%>
<%
    Configuration conf = (Configuration) pageContext.getServletContext().getAttribute(Configuration.class.getName());
    String googleMapsKey = conf.getConfigurationParamValue(ParameterNames.GOOGLE_MAPS_API_KEY);
%>
<!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="gwt:property" content="locale=<%=request.getLocale()%>">
    <title>Map4RDF</title>
    
    <script type="text/javascript" language="javascript" src="http://maps.google.com/maps?gwt=1&amp;file=api&amp;v=2&amp;sensor=true&amp;key=<%=googleMapsKey%>" ></script>
    <script type="text/javascript" language="javascript" src="es.upm.fi.dia.oeg.map4rdf.map4rdf/es.upm.fi.dia.oeg.map4rdf.map4rdf.nocache.js"></script>
  </head>

  <body>    
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
  </body>
</html>