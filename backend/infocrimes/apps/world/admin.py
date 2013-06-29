from django.contrib.gis import admin
from models import WorldBorder, CityBorder

#admin.site.register(WorldBorder, admin.GeoModelAdmin)

admin.site.register(WorldBorder, admin.OSMGeoAdmin)
admin.site.register(CityBorder, admin.OSMGeoAdmin)
