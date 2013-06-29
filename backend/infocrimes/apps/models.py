# coding=UTF-8
from django.contrib.gis.db import models
from managers import DeletedManager, ActiveManager, AllManager


class DefaultFields(models.Model):
    created = models.DateTimeField(auto_now_add=True, null=True)
    modifiend = models.DateTimeField(auto_now=True, null=True)
    deleted = models.BooleanField(default=False, db_index=True)

    objects = ActiveManager()
    deleteds = DeletedManager()
    all_objects = AllManager()

    class Meta:
        abstract = True


class TestDefaultFields(DefaultFields):
    "just for test manager"
    pass
