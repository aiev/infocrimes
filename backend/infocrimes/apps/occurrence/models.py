from django.db import connection
from django.contrib.gis.db import models
from django.contrib.gis.geos import Point, GEOSGeometry
from django.db.models.signals import post_save, pre_save
from django.dispatch import receiver

from infocrimes.apps.models import DefaultFields


class FactType(DefaultFields):
    name = models.CharField(max_length=50)

    def __unicode__(self):
        return self.name


class Fact(DefaultFields):
    name = models.CharField(max_length=50, blank=True)
    desc = models.TextField(blank=True)
    fact_type = models.ForeignKey(FactType, related_name='fact_type_name')

    # TODO: regiter who registed the fact: know owners (ssp/rs) and community users
    #owner =

    occurrence_date = models.DateTimeField(null=True)
    occurrence_address = models.CharField(max_length=200)

    lat = models.FloatField()
    lon = models.FloatField()

    geo_point = models.GeometryField(null=True, blank=True)
    map_point = models.PointField(null=True, blank=True) # ??? does not create the fucking column

    def get_fact_type_name(self):
        return self.fact_type.name

    def str_point_ll(self):
        return 'POINT(%f %f)' % (self.lon, self.lat)

    def __unicode__(self):
        return self.occurrence_address + " - " + self.str_point_ll()

"""
@receiver(post_save, sender=Fact)
def fact_post_save(sender, instance, created, raw, using, **kwargs):
    cursor = connection.cursor()
    sql = "UPDATE occurrence_fact SET map_point = POINT(%s, %s), geo_point = ST_GeomFromText('POINT(%s %s)', 4326) WHERE id = %s" % (instance.lon, instance.lat, instance.lon, instance.lat, instance.id)

    import sys
    print >>sys.stderr, sql
    cursor.execute(sql)
"""


@receiver(pre_save, sender=Fact)
def fact_pre_save(sender, instance, **kwargs):
    instance.geo_point = GEOSGeometry('POINT(%s %s)' % (instance.lon, instance.lat))
    #instance.map_point = 'POINT(%s %s)' % (instance.lon, instance.lat)
