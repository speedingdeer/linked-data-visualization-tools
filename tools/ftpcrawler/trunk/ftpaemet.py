#!/usr/bin/python3

"""* Copyright (c) ONTOLOGY ENGINEERING GROUP: UNIVERSIDAD POLITECNICA DE MADRID, 2011
* Todos los derechos reservados.
* Titulo: AEMET FTP CSV2RDF(N3) Conversor
* Autor: Jose Mora """

from ftplib import FTP
from pickle import dump, load
import os, time
from fileProcessor import FileProcessor

gettime = lambda l: "%s-%s-%s"%(l[5], l[6], l[7])

#pretend this class is not here (5 lines)
class MyFTP(FTP):
  def dir(self, d='.'):
    l = []
    super(MyFTP, self).dir(d, lambda e: l.append(e.split()))
    return l

def dlfile(furl):
  global conn
  d = os.path.dirname(furl)
  if not os.path.exists(d):
    os.makedirs(d)
  with open(furl, 'wb') as out:
    try:
      conn.retrbinary('RETR ' + furl, lambda data: out.write(data))
    except Exception as e:
      print("Exception: %s\nRetrying in 60s: %s"%(str(e.args), furl))
      time.sleep(60)
      conn = MyFTP('ftpdatos.aemet.es', 'Anonymous', '', timeout=60)
      dlfile(furl)

def dlDir(base, times):
  global conn
  newtimes = {}
  for e in conn.dir(base):
    name = base + '/' + e[8]
    if e[0][0] == 'd':
      newtimes.update(dlDir(name, times))
    elif ((not name in times or times[name] != gettime(e)) and name[-13:] == '_datos.csv.gz'):
      dlfile(name)
      fp = FileProcessor(name)
      fp.start()
    newtimes[name] = gettime(e)
  return newtimes


def doit():
  global conn
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
  

if __name__ == "__main__":
  doit()


