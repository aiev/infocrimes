from infocrimes.apps.occurrence.models import Fact, FactType
from rest_framework import serializers
from rest_framework.serializers import RelatedField


class FactSerializer(serializers.ModelSerializer):
    fact_type_name = serializers.CharField(source='get_fact_type_name', read_only=True)

    class Meta:
        model = Fact
        fields = ('id', 'name', 'desc', 'occurrence_date', 'occurrence_address', 'lon', 'lat', 'fact_type', 'fact_type_name', 'geo_point', 'map_point')


class FactTypeSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = FactType
        fields = ('id', 'name')
