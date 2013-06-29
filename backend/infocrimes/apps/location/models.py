from django.contrib.gis.db import models
from infocrimes.apps.models import DefaultFields


class Country(DefaultFields):
    name = models.CharField(max_length=50)

    def __unicode__(self):
        return self.name


class State(DefaultFields):
    name = models.CharField(max_length=50)

    def __unicode__(self):
        return self.name


class City(DefaultFields):
    name = models.CharField(max_length=50)

    def __unicode__(self):
        return self.name
