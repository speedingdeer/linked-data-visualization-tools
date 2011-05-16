"""* Copyright (c) ONTOLOGY ENGINEERING GROUP: UNIVERSIDAD POLITÉCNICA DE MADRID, 2011
* Todos los derechos reservados.
* Título: AEMET FTP CSV2RDF(N3) Conversor
* Autor: José Mora López"""

from threading import Thread
import gzip, datetime

prefixes =('@base <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix aemet: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix observacion: <http://aemet.linkeddata.es/resource/Observacion/> .\n' +
           '@prefix prop: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix estacion: <http://aemet.linkeddata.es/resource/Estacion/> .\n'+
           '@prefix intervalo: <http://aemet.linkeddata.es/resource/Intervalo/> .\n'+
           '@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .\n'+
           '@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n' +
           '@prefix propiedadAmbientalSobreViento: <http://aemet.linkeddata.es/ontology/PropiedadAmbientalSobreViento/> .\n' +
           '@prefix propiedadAmbientalSobreRadiacion: <http://aemet.linkeddata.es/ontology/PropiedadAmbientalSobreRadiacion/> .\n' +
           '@prefix propiedadAmbientalSobreTemperatura: <http://aemet.linkeddata.es/ontology/PropiedadAmbientalSobreTemperatura/> .\n' +
           '@prefix propiedadAmbientalSobrePrecipitacion: <http://aemet.linkeddata.es/ontology/PropiedadAmbientalSobrePrecipitacion/> .\n' +
           '@prefix propiedadAmbientalSobreHumedad: <http://aemet.linkeddata.es/ontology/PropiedadAmbientalSobreHumedad/> .\n' +
           '@prefix propiedadAmbiental: <http://aemet.linkeddata.es/ontology/PropiedadAmbiental/> .\n' +
           '@prefix propiedadAmbientalSobrePresion: <http://aemet.linkeddata.es/ontology/PropiedadAmbientalSobrePresion/> .\n' +
           '@prefix property: <http://aemet.linkeddata.es/ontology/Property/> .\n' +
           '@prefix time: <http://www.w3.org/2006/time#> .\n' +
           '@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n\n')

obsP = ('observacion:Observacion_en_%s_de_%s_sobre_%s a aemet:Observacion ;\n' +
        '\trdfs:label "Observacion_en_%s_de_%s_sobre_%s"@es ;\n' +
        '\tprop:valorDelDatoObservado "%s" ;\n' +
        '\tprop:calidadDelDatoObservado "%s" ;\n' +
        '\tssn:observedProperty %s:%s ;\n' +
        '\tssn:featureOfInterest aemet:condicionMeteorologica ;\n' +
        '\tssn:observedBy estacion:Estacion_%s ;\n' +
        '\tprop:observadaEnIntervalo intervalo:Diezminutal_desde_%s ;\n' +
        '\t.\n\n')
        
timeP = ('intervalo:Diezminutal_desde_%s a time:Interval ;\n' +
         '\ttime:hasBeginning <http://aemet.linkeddata.es/resource/Instante/Instante_%s> ;\n' +
         '\ttime:hasDurationDescription <http://aemet.linkeddata.es/resource/Duracion/Diezminutal> ; \n' + 
         '\t.\n\n' +
         '<http://aemet.linkeddata.es/resource/Duracion/Diezminutal> a time:DurationDescription ;\n' +
         '\ttime:minutes 10 ;\n' +
         '\t.\n\n' +
         '<http://aemet.linkeddata.es/resource/Instante/Instante_%s> a time:Instant ; \n' +
         '\ttime:inDateTime <http://aemet.linkeddata.es/resource/TiempoFecha/TiempoFecha_%s> ;\n' +
         '\t.\n\n' +
         '<http://aemet.linkeddata.es/resource/TiempoFecha/TiempoFecha_%s> a time:DateTimeDescription ;\n' +
         '\ttime:unitType time:unitMinute ;\n'+
         '\ttime:minute %d ;\n' +
         '\ttime:hour %d ;\n' +
         '\ttime:day %d ;\n' +
         '\ttime:month %d ;\n' +
         '\ttime:year %d ;\n' +
         '\t.\n\n')

p = {'RVIENTO' : 'propiedadAmbientalSobreViento', 'DV10m' : 'propiedadAmbientalSobreViento', 'DMAX10m' : 'propiedadAmbientalSobreViento',
     'VV10m' : 'propiedadAmbientalSobreViento', 'VMAX10m' : 'propiedadAmbientalSobreViento', 'INSO' : 'propiedadAmbientalSobreRadiacion',
     'RAGLOB' : 'propiedadAmbientalSobreRadiacion', 'HTAMIN1h' : 'propiedadAmbientalSobreTemperatura',
     'TAMAX1h' : 'propiedadAmbientalSobreTemperatura', 'TAMIN1h' : 'propiedadAmbientalSobreTemperatura',
     'TA' : 'propiedadAmbientalSobreTemperatura', 'HTAMAX1h' : 'propiedadAmbientalSobreTemperatura',
     'TSS5cm' : 'propiedadAmbientalSobreTemperatura', 'TAMAX10m' : 'propiedadAmbientalSobreTemperatura',
     'TAMIN10m' : 'propiedadAmbientalSobreTemperatura', 'TS' : 'propiedadAmbientalSobreTemperatura',
     'PLIQTP' : 'propiedadAmbientalSobrePrecipitacion', 'NIEVE' : 'propiedadAmbientalSobrePrecipitacion',
     'PREC' : 'propiedadAmbientalSobrePrecipitacion', 'HR' : 'propiedadAmbientalSobreHumedad',
     'TPR' : 'propiedadAmbientalSobreHumedad', 'TPRE' : 'propiedadAmbiental', 'VIS' : 'propiedadAmbiental',
     'PRES' : 'propiedadAmbientalSobrePresion', 'GEO700' : 'propiedadAmbientalSobrePresion', 'GEO925' : 'propiedadAmbientalSobrePresion',
     'GEO850' : 'propiedadAmbientalSobrePresion', 'PSOLTP' : 'propiedadAmbientalSobrePresion',
     'PRES_nmar' : 'propiedadAmbientalSobrePresion', 'BAT' : 'property', 'BATH' : 'property'}

     
class FileProcessor(Thread):

  def __init__(self, filename):
    Thread.__init__(self)
    self.filename = filename
  
  def run(self):
    with open(self.filename[:-7]+".rdf", 'w', encoding='utf8') as rdffile:
      rdffile.write(prefixes)
      with gzip.open(self.filename, 'r') as csvfile:
        for l in csvfile:
          e = (l.decode('mbcs'))[:-1].split(',')
          for i in range(7, len(e), 2):
            ei = e[i].split('=')
            rdffile.write(obsP%(e[4], e[0], ei[0], e[4], e[0], ei[0], ei[1], e[i+1].split('=')[1], p[ei[0]], ei[0], e[0], e[4]))
        st = datetime.datetime.fromtimestamp(int(e[4])/1000)
        rdffile.write(timeP%(e[4], e[4], e[4], e[4], e[4], st.minute, st.hour, st.day, st.month, st.year))
