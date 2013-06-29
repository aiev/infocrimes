# coding=UTF-8
from django.test.client import Client
from django.test import TestCase

from infocrimes.apps.occurrence.models import Fact, FactType



class Test(TestCase):
    def setUp(self):
        self.c = Client()

        self.facttype, created = FactType.objects.get_or_create(
            name='test type'
        )


    def test_add_fact(self):

        response = self.c.post('/api/facts/', {
            'fact_type': self.facttype.id,
            'occurrence_address': 'test address',
            'occurrence_date': '2012-05-14T01:47:00Z',
            'lat': '10.10',
            'lon': '10.10',
            'name': 'test name',
            'desc': 'test desc',
            'map_point': '',
            'geo_point': '',
        })

        self.assertEqual(201, response.status_code)

    def test_findby(self):
        response = self.c.get('/api/facts/', {
            'maxX': '0.0,0.0',
            'maxY': '20.20,0.0',
            'minX': '0.0,20.20',
            'minY': '20.20,20.20',
        })

        self.assertEqual(200, response.status_code)


class ModelTest(TestCase):
    def test_fact_exists(self):

        try:
            from infocrimes.apps.occurrence.models import Fact
        except ImportError:
            self.fail('Erro no Modelo Fact')

    def test_facttype_exists(self):

        try:
            from infocrimes.apps.occurrence.models import FactType
        except ImportError:
            self.fail('Erro no Modelo FactType')
