from django.contrib.gis.db import models


class ActiveManager(models.GeoManager):
    def get_query_set(self):
        "Active by default"
        return super(ActiveManager, self).get_query_set().filter(deleted=False)


class DeletedManager(models.GeoManager):
    def get_query_set(self):
        "Show canceled content"
        return super(DeletedManager, self).get_query_set().filter(deleted=True)

class AllManager(models.GeoManager):
    def get_query_set(self):
        "Show all content"
        return super(AllManager, self).get_query_set()
