"""* Copyright (c) ONTOLOGY ENGINEERING GROUP: UNIVERSIDAD POLITÉCNICA DE MADRID, 2011
* Todos los derechos reservados.
* Título: AEMET FTP CSV2RDF(N3) Conversor
* Autor: José Mora López"""

from threading import Thread
import gzip
from urllib.parse import quote

prefixes =('@base <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix aemet: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix observacion: <http://aemet.linkeddata.es/resource/Observacion/> .\n' +
           '@prefix prop: <http://aemet.linkeddata.es/ontology/property/> .\n' +
           '@prefix estacion: <http://aemet.linkeddata.es/resource/Estacion/> .\n'+
           '@prefix ssn: <http://purl.oclc.org/NET/ssnx/ssn#> .\n'+
           '@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n' +
           '@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n\n')

obsP = ('observacion:Observacion_en_%s_de_%s_sobre_%s a aemet:Observacion ;\n' +
        '\trdfs:label "Observacion_en_%s_de_%s_sobre_%s"@es ;\n' +
        '\tprop:valorDelDatoObservado "%s" ;\n' +
        '\tprop:calidadDelDatoObservado "%s" ;\n' +
        '\tssn:observedProperty aemet:%s ;\n' +
        '\tssn:featureOfInterest aemet:condicionMeteorologica ;\n' +
        '\tssn:observedBy estacion:Estacion_%s ;\n' +
        '\t.\n\n')

       
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
            rdffile.write(obsP%(e[4], e[0], ei[0], e[4], e[0], ei[0], ei[1], e[i+1].split('=')[1], ei[0], e[0]))
