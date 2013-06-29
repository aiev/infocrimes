from django.contrib.gis import admin
from models import Fact, FactType


class FactAdmin(admin.OSMGeoAdmin):
    exclude = ('geo_point',)
    list_filter = ('fact_type',)

    search_fields = ["name", "occurrence_address", "occurrence_date", "desc", "fact_type__name"]


admin.site.register(Fact, FactAdmin)
admin.site.register(FactType, admin.OSMGeoAdmin)
