from django.contrib.gis.geos import fromstr, Polygon, GEOSGeometry

from rest_framework import viewsets

from infocrimes.apps.occurrence.models import Fact, FactType
from infocrimes.apps.occurrence.serializers import FactSerializer, FactTypeSerializer


class FactTypeViewSet(viewsets.ModelViewSet):

    queryset = FactType.objects.all()
    serializer_class = FactTypeSerializer


class FactViewSet(viewsets.ModelViewSet):

    queryset = Fact.objects.select_related('fact_type').all()
    serializer_class = FactSerializer
    paginate_by = 3000

    def get_queryset(self):
        """lat = float(self.request.GET.get('lat', 0.0))
        lon = float(self.request.GET.get('lon', 0.0))
        distance = int(self.request.GET.get('dist', 1))

        pnt = fromstr('POINT(%f %f)' % (lon, lat))

        return Fact.objects.filter(geo_point__distance_lte=(pnt, distance))
        """
        """
                            +----------o Upper right; (max_x, max_y)
                            |          |
                            |          |
                            |          |
  Lower left (min_x, min_y) o----------+
        """

        max_x = self.request.GET.get('maxX', '0.0,0.0')
        max_y = self.request.GET.get('maxY', '0.0,0.0')
        min_x = self.request.GET.get('minX', '0.0,0.0')
        min_y = self.request.GET.get('minY', '0.0,0.0')

        p1 = tuple(float(p) for p in max_x.split(','))
        p2 = tuple(float(p) for p in max_y.split(','))
        p3 = tuple(float(p) for p in min_x.split(','))
        p4 = tuple(float(p) for p in min_y.split(','))

        bounds = Polygon((p1, p3, p4, p2, p1))

        return Fact.objects.filter(geo_point__intersects=bounds)

"""
    def list(self, request):
        lat = float(request.GET.get('lat', 0.0))
        lon = float(request.GET.get('lon', 0.0))
        distance = int(request.GET.get('d', 1))

        pnt = fromstr('POINT(%f %f)' % (lon, lat))

        queryset = Fact.objects.filter(geo_point__distance_lte=(pnt, distance))
        serializer = FactSerializer(queryset, many=True)

        return Response(serializer.data)

    def retrieve(self, request, pk=None):
        queryset = Fact.objects.all()
        fact = get_object_or_404(queryset, pk=pk)
        serializer = FactSerializer(fact)
        return Response(serializer.data)
"""
