import os
from django.contrib.gis.utils import LayerMapping
from models import CityBorder


cityborder_mapping = {
    'name': 'NAME',
    'country': 'COUNTRY',
    'population': 'POPULATION',
    'capital': 'CAPITAL',
    'geom': 'MULTIPOINT',
}

city_shp = os.path.abspath(os.path.join(os.path.dirname(__file__), 'data/cities/cities.shp'))

def run(verbose=True):
    lm = LayerMapping(CityBorder, city_shp, cityborder_mapping,
                      transform=False, encoding='iso-8859-1')

    lm.save(strict=True, verbose=verbose)
