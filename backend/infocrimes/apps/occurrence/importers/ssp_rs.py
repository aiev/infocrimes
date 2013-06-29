#!/usr/bin/python
import os
import sys
import csv
import datetime
import time
import simplejson
import urllib

# add project to pythonpath
sys.path.append(os.path.abspath('../../../../'))

# setup django environ
from django.core.management import setup_environ
from django.contrib.gis.geos import Point
from django.contrib.gis.measure import D

from infocrimes import settings; setup_environ(settings)
from infocrimes.apps.occurrence.models import Fact



GEOCODE_BASE_URL = 'http://maps.google.com/maps/api/geocode/json'


def to_facttype(facttype):
    f = facttype.strip()

    if f == 'ROUBO DE VEICULO':
        return 3

    if f == 'FURTO DE VEICULO':
        return 2

    return 0


def to_datetime(str):
    return datetime.datetime.strptime(str, '%d/%m/%Y %H:%M')


def get_ll(address):
    geo_args = {
        'address': address + ", porto alegre",
        'sensor': "false"
    }

    url = GEOCODE_BASE_URL + '?' + urllib.urlencode(geo_args)
    r = simplejson.load(urllib.urlopen(url))

    try:
        lat = float(r['results'][0]['geometry']['location']['lat'])
        lng = float(r['results'][0]['geometry']['location']['lng'])

        return Point(lng, lat)
    except:
        return Point(0, 0)


def adapt(row):
    facttype = row[0]
    date = row[2]
    address = "%s, %s" % (row[5], row[6]) # <street>, <district>
    x = int(row[7])
    y = int(row[8])

    fact = Fact()
    fact.fact_type_id = to_facttype(facttype)
    #fact.name

    fact.point = get_ll(address)
    fact.lat = fact.point.x
    fact.lon = fact.point.y

    fact.occurrence_address = address
    fact.occurrence_date = to_datetime(date)

    return fact


def main(file_path):
    print 'Importing file: %s' % file_path
    t = Fact.objects.count()

    # FATO;ANO_MES;DATA;DIA_FATO;FX_HORARIO;LOGRADOURO;BAIRRO;POSICAO_X;POSICAO_Y;
    with open(file_path, 'rb') as csvfile:
        spamreader = csv.reader(csvfile, delimiter=';')
        i = 1
        for row in spamreader:
            if i > t:
                fact = adapt(row)
                print 'Saving fact on %f, %f' % (fact.lat, fact.lon)
                fact.save()

                #print 'Wait 1s.'
                #time.sleep(1)

            i = i + 1


if __name__ == "__main__":

    if len(sys.argv) != 2:
        print 'usage: %s <inputfile>' % os.path.basename(__file__)
        sys.exit(0)

    main(sys.argv[1:][0])

