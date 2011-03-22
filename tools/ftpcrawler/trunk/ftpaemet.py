#!/usr/bin/env python3

from ftplib import FTP
from pickle import dump, load
import os, time, gzip
from urllib.parse import quote

gettime = lambda l: "%s-%s-%s"%(l[5], l[6], l[7])
fieldsWithoutName = ['indsinop', 'provincia', 'lat', 'long', 'tiempo_inicio', 'tiempo_fin']
prefixes =('@base <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix aemet: <http://aemet.linkeddata.es/ontology/> .\n' +
           '@prefix geopos: <http://www.w3.org/2003/01/geo/wgs84_pos#> .\n' +
           '@prefix observacion: <http://aemet.linkeddata.es/resource/Observacion/> .\n' +
           '@prefix punto: <http://aemet.linkeddata.es/resource/Point/> .\n' +
           '@prefix prop: <http://aemet.linkeddata.es/ontology/property/> .\n' +
           '@prefix estacion: <http://aemet.linkeddata.es/resource/Estacion/> .\n')

#pretend this class is not here (5 lines)
class MyFTP(FTP):
  def dir(self, d='.'):
    l = []
    super(MyFTP, self).dir(d, lambda e: l.append(e.split()))
    return l

def processfile(f):
  with open(f[:-7]+".rdf", 'w', encoding='utf8') as rdffile:
    rdffile.write(prefixes)
    with gzip.open(f, 'r') as csvfile:
      for l in csvfile:
        fields = (l.decode('mbcs'))[:-1].split(',')
        #fields[1] = quote(fields[1])
        rdffile.write('observacion:o%s_%s a aemet:Observacion;\n\tprop:deEstacion estacion:Estacion_%s;\n'%(
                fields[0], fields[4], fields[0]))
        for i in [0,1,4,5]:
          rdffile.write('\tprop:%s "%s";\n'%(fieldsWithoutName[i], fields[i]))
        for i in range(6,len(fields)):
          subfields = fields[i].split('=')
          rdffile.write('\tprop:%s "%s";\n'%(subfields[0], subfields[1]))
        rdffile.write('\t.\n')

def dlfile(file):
  global conn
  d = os.path.dirname(file)
  if not os.path.exists(d):
    os.makedirs(d)
  with open(file, 'wb') as out:
    try:
      conn.retrbinary('RETR ' + file, lambda data: out.write(data))
    except Exception as e:
      print("Exception: %s\nRetrying in 60s: %s"%(str(e.args), file))
      time.sleep(60)
      conn = MyFTP('ftpdatos.aemet.es', 'Anonymous', '', timeout=60)
      dlfile(file)
  if (file[-13:] == '_datos.csv.gz'):
    processfile(file)

def dlDir(base, times):
  global conn
  newtimes = {}
  for e in conn.dir(base):
    name = base + '/' + e[8]
    if e[0][0] == 'd':
      newtimes.update(dlDir(name, times))
    elif (not name in times or times[name] != gettime(e)):
      dlfile(name)
    newtimes[name] = gettime(e)
  return newtimes

try:
  with open('status.pickle', 'rb') as old:
    times = load(old)
except:
  times = {}
base = "datos_observacion/observaciones_diezminutales"
conn = MyFTP('ftpdatos.aemet.es', 'Anonymous', '', timeout=60)
newtimes = dlDir(base, times)
conn.close()
with open('status.pickle', 'wb') as new:
  dump(newtimes, new)
  

