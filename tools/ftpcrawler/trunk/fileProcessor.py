"""* Copyright (c) ONTOLOGY ENGINEERING GROUP: UNIVERSIDAD POLITÉCNICA DE MADRID, 2011
* Todos los derechos reservados.
* Tílo: AEMET FTP CSV2RDF(N3) Conversor
* Autor: José Mora López"""

from threading import Thread
from string import Template
import gzip, time

prefixes =('@base <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix aemet: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix observation: <http://aemet.linkeddata.es/resource/Observation/> .\n' +
           '@prefix prop: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix station: <http://aemet.linkeddata.es/resource/WeatherStation/> .\n'+
           '@prefix interval: <http://aemet.linkeddata.es/resource/Interval/> .\n'+
           '@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .\n'+
           '@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n' +
           '@prefix windAmbientProperty: <http://aemet.linkeddata.es/resource/WindAmbientProperty/> .\n' +
           '@prefix radiationAmbientProperty: <http://aemet.linkeddata.es/resource/RadiationAmbientProperty/> .\n' +
           '@prefix temperatureAmbientProperty: <http://aemet.linkeddata.es/resource/TemperatureAmbientProperty/> .\n' +
           '@prefix precipitationAmbientProperty: <http://aemet.linkeddata.es/resource/PrecipitationAmbientProperty/> .\n' +
           '@prefix humidityAmbientProperty: <http://aemet.linkeddata.es/resource/HumidityAmbientProperty/> .\n' +
           '@prefix ambientProperty: <http://aemet.linkeddata.es/resource/AmbientProperty/> .\n' +
           '@prefix pressureAmbientProperty: <http://aemet.linkeddata.es/resource/PressureAmbientProperty/> .\n' +
           '@prefix property: <http://aemet.linkeddata.es/resource/Property/> .\n' +
           '@prefix time: <http://www.w3.org/2006/time#> .\n' +
           '@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n' +
           '@prefix tz-world: <http://www.w3.org/2006/timezone-world#> .\n' +
           '@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n\n')

obsT = Template ('observation:at_${time}_of_${stationId}_on_${prop} a aemet:Observation ;\n' +
        '\trdfs:label "Observación: $time de: $stationId sobre: $prop"@es ;\n' +
        '\trdfs:label "Observation at: $time from: $stationId about: $prop"@en ;\n' +
        '\tprop:valueOfObservedData "$value"^^xsd:$type ;\n' +
        '\tprop:observedDataQuality "$quality"^^xsd:int ;\n' +
        '\tssn:observedProperty $propClass:$prop ;\n' +
        '\tssn:featureOfInterest aemet:meteorologicalCondition ;\n' +
        '\tssn:observedBy station:id$stationId ;\n' +
        '\tprop:observedInInterval interval:tenMinutes_since_$time ;\n' +
        '\t.\n\n')

timeT = Template ('interval:tenMinutes_since_$time a time:Interval ;\n' +
         '\ttime:hasBeginning <http://aemet.linkeddata.es/resource/Instant/t$time> ;\n' +
         '\ttime:hasDurationDescription <http://aemet.linkeddata.es/resource/DurationDescription/tenMinutes> ; \n' +
         '\t.\n\n' +
         '<http://aemet.linkeddata.es/resource/DateTimeDescription/tenMinutes> a time:DurationDescription ;\n' +
         '\ttime:minutes "10"^^xsd:int ;\n' +
         '\t.\n\n' +
         '<http://aemet.linkeddata.es/resource/Instant/t$time> a time:Instant ; \n' +
         '\ttime:inDateTime <http://aemet.linkeddata.es/resource/DateTimeDescription/dtd$time> ;\n' +
         '\t.\n\n' +
         '<http://aemet.linkeddata.es/resource/DateTimeDescription/dtd$time> a time:DateTimeDescription ;\n' +
         '\ttime:unitType time:unitMinute ;\n'+
         '\ttime:minute "$min"^^xsd:int ;\n' +
         '\ttime:hour "$hour"^^xsd:int ;\n' +
         '\ttime:day "$day"^^xsd:int ;\n' +
         '\ttime:dayOfWeek time:$dow ;\n' +
         '\ttime:dayOfYear "$doy"^^xsd:int ;\n' +
         '\ttime:week "$week"^^xsd:int ;\n' +
         '\ttime:month "$month"^^xsd:int ;\n' +
         '\ttime:year "$year"^^xsd:int ;\n' +
         '\ttime:timeZone tz-world:TZT ;\n' +
         '\ttime:inXSDDateTime "$xsddt"^^xsd:dateTime ;\n' +
         '\t.\n\n')

p = {"TPRE":"ambientProperty", "VIS":"ambientProperty", "HR":"humidityAmbientProperty", "TPR":"humidityAmbientProperty",
     "NIEVE":"precipitationAmbientProperty", "PLIQTP":"precipitationAmbientProperty", "PREC":"precipitationAmbientProperty",
     "GEO700":"pressureAmbientProperty", "GEO850":"pressureAmbientProperty", "GEO925":"pressureAmbientProperty",
     "PRES":"pressureAmbientProperty", "PRES_nmar":"pressureAmbientProperty", "PSOLTP":"pressureAmbientProperty", "BAT":"property",
     "BATH":"property", "INSO":"radiationAmbientProperty", "RAGLOB":"radiationAmbientProperty", "HTAMAX1h":"temperatureAmbientProperty",
     "HTAMIN1h":"temperatureAmbientProperty", "TA":"temperatureAmbientProperty", "TAMAX10m":"temperatureAmbientProperty",
     "TAMAX1h":"temperatureAmbientProperty", "TAMIN10m":"temperatureAmbientProperty", "TAMIN1h":"temperatureAmbientProperty",
     "TS":"temperatureAmbientProperty", "TSS5cm":"temperatureAmbientProperty", "DMAX10m":"windAmbientProperty", "DV10m":"windAmbientProperty",
     "RVIENTO":"windAmbientProperty", "VMAX10m":"windAmbientProperty", "VV10m":"windAmbientProperty"}
#    "STDDV":"uF1", "STDVV":"uF2", "VVU10m":"uF3", "DVU10m":"uF4", "STDVVU":"uF5", "STDDVU":"uF6",
#    "VMAXU10m":"uF7", "DMAXU10m":"uF8", "CA":"uF9", "TSS20cm":"uF10", "PACUTP":"uF11"}

dow = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']

typeof = lambda x: "decimal" if "." in x else "time" if ":"  in x else "int"

fixt = lambda x: x + ":00Z" if ":" in x else x

humantime = lambda x: time.strftime("%Y-%m-%dT%H:%M:%SZ", time.localtime(x/1000))
     
class FileProcessor(Thread):

  def __init__(self, filename):
    Thread.__init__(self)
    self.filename = filename
  
  def run(self):
    with open(self.filename[:-7]+".ttl", 'w', encoding='utf8') as rdffile:
      rdffile.write(prefixes)
      with gzip.open(self.filename, 'r') as csvfile:
        for l in csvfile:
          e = (l.decode('iso-8859-1'))[:-1].split(',')
          for i in range(7, len(e), 2):
            ei = e[i].split('=')
            if ei[0] in p:
              rdffile.write(obsT.substitute({'time': e[4], 'stationId': e[0], 'prop': ei[0], 'value': fixt(ei[1]),
                                             'type': typeof(ei[1]), 'quality': e[i+1].split('=')[1], 'propClass': p[ei[0]]}))
        st = time.gmtime(int(e[4])/1000)
        rdffile.write(timeT.substitute({'time': e[4], 'min': st[4], 'hour': st[3], 'day': st[2], 'dow': dow[st[6]], 'doy': st[7],
                                        'week': st[7]//7, 'month': st[1], 'year': st[0], 'xsddt': humantime(int(e[4]))}))

