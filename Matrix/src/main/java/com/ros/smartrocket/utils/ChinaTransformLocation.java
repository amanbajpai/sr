package com.ros.smartrocket.utils;

import android.location.Location;

import com.ros.smartrocket.map.polygon.Point;
import com.ros.smartrocket.map.polygon.Polygon;

public class ChinaTransformLocation {
    private static final String TAG = ChinaTransformLocation.class.getSimpleName();
    public static final float BAIDU_MAP_COORDINATE_OFFSET = 0.006f;
    private static final double PI = 3.14159265358979323;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;
    private static final double OPEN_ANGLE = 180.0;

    private static final double MN_0_1 = 0.1;
    private static final double MN_0_2 = 0.2;
    private static final double MN_2 = 2.0;
    private static final double MN_3 = 3.0;
    private static final double MN_6 = 6.0;
    private static final double MN_12 = 12.0;
    private static final double MN_20 = 20.0;
    private static final double MN_30 = 30.0;
    private static final double MN_35 = 35.0;
    private static final double MN_40 = 40.0;
    private static final double MN_100 = 100.0;
    private static final double MN_105 = 105.0;
    private static final double MN_150 = 150.0;
    private static final double MN_160 = 160.0;
    private static final double MN_300 = 300.0;
    private static final double MN_320 = 320.0;

    public static void transformToChinaLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (!outOfChina(latitude, longitude)) {
                double dLat = transformLat(longitude - MN_105, latitude - MN_35);
                double dLon = transformLon(longitude - MN_105, latitude - MN_35);
                double radLat = latitude / OPEN_ANGLE * PI;
                double magic = Math.sin(radLat);

                magic = 1 - EE * magic * magic;

                double sqrtMagic = Math.sqrt(magic);

                dLat = (dLat * OPEN_ANGLE) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
                dLon = (dLon * OPEN_ANGLE) / (A / sqrtMagic * Math.cos(radLat) * PI);

                latitude = latitude + dLat;
                longitude = longitude + dLon;

                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }
    }

    public static void transformFromChinaWorldLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (!outOfChina(latitude, longitude)) {
                double dLat = transformLat(longitude - MN_105, latitude - MN_35);
                double dLon = transformLon(longitude - MN_105, latitude - MN_35);
                double radLat = latitude / OPEN_ANGLE * PI;
                double magic = Math.sin(radLat);

                magic = 1 - EE * magic * magic;

                double sqrtMagic = Math.sqrt(magic);

                dLat = (dLat * OPEN_ANGLE) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
                dLon = (dLon * OPEN_ANGLE) / (A / sqrtMagic * Math.cos(radLat) * PI);

                latitude = latitude - dLat;
                longitude = longitude - dLon;

                location.setLatitude(latitude);
                location.setLongitude(longitude);
            }
        }
    }

    public static void transformToBaiduLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            transformToChinaLocation(location);

            double x_pi = PI * 3000.0 / 180.0;
            double z = Math.sqrt(longitude * longitude + latitude * latitude) + 0.00002 * Math.sin(latitude * x_pi);
            double theta = Math.atan2(latitude, longitude) + 0.000003 * Math.cos(longitude * x_pi);

            location.setLatitude(z * Math.sin(theta) + 0.006);
            location.setLongitude(z * Math.cos(theta) + 0.0065);
        }
    }

    public static void transformFromBaiduToWorldLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude() - 0.006;
            double longitude = location.getLongitude() - 0.0065;

            double x_pi = PI * 3000.0 / 180.0;
            double z = Math.sqrt(longitude * longitude + latitude * latitude) - 0.00002 * Math.sin(latitude * x_pi);
            double theta = Math.atan2(latitude, longitude) - 0.000003 * Math.cos(longitude * x_pi);

            location.setLatitude(z * Math.sin(theta));
            location.setLongitude(z * Math.cos(theta));

            transformFromChinaWorldLocation(location);
        }
    }

    public static boolean outOfChina(double lat, double lon) {
        boolean outOfChina = !inChina(lat, lon) || inHongKong(lat, lon) || inMakao(lat, lon);

        //L.i(TAG, "outOfChina = " + outOfChina);

        return outOfChina;
    }

    public static boolean inChina(double lat, double lon) {
        Polygon polygon = Polygon.Builder()
                .addVertex(new Point(17.895114303749153, 108.25927734375))
                .addVertex(new Point(21.53484700204879, 108.0450439453125))
                .addVertex(new Point(21.733988636412214, 107.2430419921875))
                .addVertex(new Point(21.99908185836153, 106.710205078125))
                .addVertex(new Point(22.497332432882345, 106.556396484375))
                .addVertex(new Point(22.786310789104277, 106.8145751953125))
                .addVertex(new Point(22.938159639316396, 106.5234375))
                .addVertex(new Point(22.882501434370063, 106.2652587890625))
                .addVertex(new Point(22.968509022673924, 106.2158203125))
                .addVertex(new Point(22.988738160960725, 105.9906005859375))
                .addVertex(new Point(22.933100746980408, 105.875244140625))
                .addVertex(new Point(23.064570430524864, 105.721435546875))
                .addVertex(new Point(23.074678175027337, 105.5731201171875))
                .addVertex(new Point(23.180763583129444, 105.5401611328125))
                .addVertex(new Point(23.387640227334956, 105.3204345703125))
                .addVertex(new Point(23.25648743787913, 105.2105712890625))
                .addVertex(new Point(23.175713800385203, 104.9029541015625))
                .addVertex(new Point(23.079731762449878, 104.8095703125))
                .addVertex(new Point(22.938159639316396, 104.8370361328125))
                .addVertex(new Point(22.816694126899844, 104.69970703125))
                .addVertex(new Point(22.847070687839064, 104.56787109375))
                .addVertex(new Point(22.684984142872107, 104.3646240234375))
                .addVertex(new Point(22.781246241104427, 104.2437744140625))
                .addVertex(new Point(22.831883254915766, 104.26025390625))
                .addVertex(new Point(22.766051469126936, 104.0789794921875))
                .addVertex(new Point(22.517631421923767, 103.9910888671875))
                .addVertex(new Point(22.760986169250472, 103.677978515625))
                .addVertex(new Point(22.791375149053486, 103.63677978515625))
                .addVertex(new Point(22.5861184893211, 103.52691650390625))
                .addVertex(new Point(22.814162440824852, 103.3209228515625))
                .addVertex(new Point(22.44911039888609, 103.0572509765625))
                .addVertex(new Point(22.487181821139295, 102.92266845703125))
                .addVertex(new Point(22.616546265412566, 102.84027099609375))
                .addVertex(new Point(22.768584048597113, 102.47772216796875))
                .addVertex(new Point(22.405950148725736, 102.139892578125))
                .addVertex(new Point(22.43387890178297, 101.9091796875))
                .addVertex(new Point(22.380555501421533, 101.8927001953125))
                .addVertex(new Point(22.49479484975443, 101.74713134765625))
                .addVertex(new Point(22.177231792821342, 101.5740966796875))
                .addVertex(new Point(21.815608175662636, 101.7718505859375))
                .addVertex(new Point(21.227941905058174, 101.8048095703125))
                .addVertex(new Point(21.125497636606276, 101.7279052734375))
                .addVertex(new Point(21.227941905058174, 101.4697265625))
                .addVertex(new Point(21.17672864097083, 101.2884521484375))
                .addVertex(new Point(21.75439787437119, 101.085205078125))
                .addVertex(new Point(21.442843107187667, 100.5523681640625))
                .addVertex(new Point(21.54506606426624, 100.426025390625))
                .addVertex(new Point(21.468405577312012, 100.2337646484375))
                .addVertex(new Point(21.585935114788494, 100.118408203125))
                .addVertex(new Point(22.039821650237034, 99.9041748046875))
                .addVertex(new Point(22.151795575397756, 99.1461181640625))
                .addVertex(new Point(23.024131861805987, 99.51416015625))
                .addVertex(new Point(23.19591178780951, 98.9263916015625))
                .addVertex(new Point(24.09661861127878, 98.668212890625))
                .addVertex(new Point(24.078439830932368, 98.0584716796875))
                .addVertex(new Point(24.017616222053803, 97.90672302246094))
                .addVertex(new Point(23.994407876965454, 97.88475036621094))
                .addVertex(new Point(23.977469416068715, 97.89436340332031))
                .addVertex(new Point(23.98060633617294, 97.85728454589844))
                .addVertex(new Point(23.845649887659352, 97.646484375))
                .addVertex(new Point(23.936054914599815, 97.52838134765625))
                .addVertex(new Point(24.15803125782562, 97.74398803710938))
                .addVertex(new Point(24.185594686815648, 97.74398803710938))
                .addVertex(new Point(24.22442395705757, 97.72613525390625))
                .addVertex(new Point(24.26198940079632, 97.76458740234375))
                .addVertex(new Point(24.300795367123083, 97.71926879882812))
                .addVertex(new Point(24.308304829404758, 97.6629638671875))
                .addVertex(new Point(24.352101162808903, 97.66845703125))
                .addVertex(new Point(24.370866384303955, 97.71652221679688))
                .addVertex(new Point(24.45340026678761, 97.67120361328125))
                .addVertex(new Point(24.443399034681697, 97.52975463867188))
                .addVertex(new Point(24.741842491684054, 97.5531005859375))
                .addVertex(new Point(24.76927845059527, 97.57369995117188))
                .addVertex(new Point(24.840334320878735, 97.70416259765625))
                .addVertex(new Point(24.830364024647107, 97.76870727539062))
                .addVertex(new Point(24.85902646154975, 97.80303955078125))
                .addVertex(new Point(24.8851907122672, 97.7728271484375))
                .addVertex(new Point(24.8851907122672, 97.74398803710938))
                .addVertex(new Point(25.084355134867106, 97.723388671875))
                .addVertex(new Point(25.267052312190323, 97.83737182617188))
                .addVertex(new Point(25.216123503743905, 97.92388916015625))
                .addVertex(new Point(25.46311452925943, 98.1298828125))
                .addVertex(new Point(25.898761936567023, 98.701171875))
                .addVertex(new Point(27.391278222579277, 98.712158203125))
                .addVertex(new Point(27.63487379134253, 98.492431640625))
                .addVertex(new Point(27.49852672279832, 98.349609375))
                .addVertex(new Point(28.497660832963472, 97.679443359375))
                .addVertex(new Point(28.50731557844178, 97.53662109375))
                .addVertex(new Point(28.22697003891834, 97.3388671875))
                .addVertex(new Point(28.57487404744697, 96.56982421875))
                .addVertex(new Point(29.35345166863502, 96.075439453125))
                .addVertex(new Point(29.075375179558346, 95.25146484375))
                .addVertex(new Point(29.305561325527698, 94.669189453125))
                .addVertex(new Point(27.76132987450523, 91.636962890625))
                .addVertex(new Point(28.04289477256162, 91.29638671875))
                .addVertex(new Point(28.16887518006332, 89.681396484375))
                .addVertex(new Point(27.332735136859146, 88.912353515625))
                .addVertex(new Point(28.081673729044283, 88.70361328125))
                .addVertex(new Point(27.887639217136517, 88.06640625))
                .addVertex(new Point(27.955591004642553, 86.385498046875))
                .addVertex(new Point(28.92163128242129, 84.30908203125))
                .addVertex(new Point(30.44867367928756, 81.551513671875))
                .addVertex(new Point(30.002516938570686, 81.18896484375))
                .addVertex(new Point(31.644028945047847, 78.75))
                .addVertex(new Point(32.54681317351514, 78.37646484375))
                .addVertex(new Point(32.657875736955305, 78.72802734375))
                .addVertex(new Point(32.379961464357315, 79.0576171875))
                .addVertex(new Point(33.37641235124676, 78.99169921875))
                .addVertex(new Point(33.815666308702774, 78.85986328125))
                .addVertex(new Point(35.37113502280101, 80.419921875))
                .addVertex(new Point(36.08462129606931, 79.21142578125))
                .addVertex(new Point(35.53222622770337, 77.783203125))
                .addVertex(new Point(36.98500309285596, 74.6630859375))
                .addVertex(new Point(37.17782559332976, 74.53125))
                .addVertex(new Point(37.42252593456306, 75.08056640625))
                .addVertex(new Point(38.46219172306828, 74.619140625))
                .addVertex(new Point(38.634036452919226, 74.15771484375))
                .addVertex(new Point(39.36827914916013, 73.564453125))
                .addVertex(new Point(39.57182223734374, 73.916015625))
                .addVertex(new Point(40.17887331434696, 74.11376953125))
                .addVertex(new Point(40.56389453066509, 75.5419921875))
                .addVertex(new Point(40.27952566881291, 75.8935546875))
                .addVertex(new Point(40.94671366508002, 76.904296875))
                .addVertex(new Point(41.0130657870063, 77.7392578125))
                .addVertex(new Point(42.032974332441405, 80.1123046875))
                .addVertex(new Point(43.29320031385282, 80.74951171875))
                .addVertex(new Point(44.91813929958515, 79.95849609375))
                .addVertex(new Point(45.29034662473615, 81.62841796875))
                .addVertex(new Point(45.19752230305685, 82.59521484375))
                .addVertex(new Point(45.84410779560204, 82.3095703125))
                .addVertex(new Point(47.144897485553976, 83.056640625))
                .addVertex(new Point(46.9502622421856, 85.23193359375))
                .addVertex(new Point(48.4146186174932, 85.84716796875))
                .addVertex(new Point(49.09545216253482, 86.98974609375))
                .addVertex(new Point(49.1242192485914, 87.890625))
                .addVertex(new Point(48.06339653776211, 88.9013671875))
                .addVertex(new Point(47.79839667295524, 90.0439453125))
                .addVertex(new Point(46.46813299215554, 91.0986328125))
                .addVertex(new Point(45.521743896993634, 90.703125))
                .addVertex(new Point(45.089035564831036, 91.03271484375))
                .addVertex(new Point(44.84029065139799, 93.8232421875))
                .addVertex(new Point(44.197959039485305, 95.20751953125))
                .addVertex(new Point(42.71473218539458, 96.43798828125))
                .addVertex(new Point(42.69858589169842, 100.546875))
                .addVertex(new Point(41.60722821271716, 104.83154296875))
                .addVertex(new Point(42.374778361114195, 107.29248046875))
                .addVertex(new Point(42.69051116998241, 110.23681640625))
                .addVertex(new Point(43.70759350405294, 111.9287109375))
                .addVertex(new Point(44.87144275016589, 111.62109375))
                .addVertex(new Point(44.68427737181224, 113.466796875))
                .addVertex(new Point(46.70973594407157, 118.828125))
                .addVertex(new Point(46.6795944656402, 120.0146484375))
                .addVertex(new Point(48.07807894349862, 118.4326171875))
                .addVertex(new Point(47.66538735632654, 117.1142578125))
                .addVertex(new Point(47.78363463526376, 115.751953125))
                .addVertex(new Point(49.809631563563094, 116.455078125))
                .addVertex(new Point(49.61070993807422, 117.8173828125))
                .addVertex(new Point(50.20503326494332, 119.3115234375))
                .addVertex(new Point(52.3755991766591, 120.76171875))
                .addVertex(new Point(52.82932091031373, 120.1904296875))
                .addVertex(new Point(53.25206880589414, 121.0693359375))
                .addVertex(new Point(53.4357192066942, 124.3212890625))
                .addVertex(new Point(53.09402405506325, 125.2880859375))
                .addVertex(new Point(52.16045455774706, 126.5625))
                .addVertex(new Point(51.23440735163458, 126.9580078125))
                .addVertex(new Point(50.17689812200105, 127.5732421875))
                .addVertex(new Point(49.468124067331644, 128.1005859375))
                .addVertex(new Point(49.49667452747044, 129.4189453125))
                .addVertex(new Point(48.80686346108517, 130.4296875))
                .addVertex(new Point(47.60616304386874, 131.1767578125))
                .addVertex(new Point(48.37084770238363, 134.6923828125))
                .addVertex(new Point(47.923704717745686, 134.5660400390625))
                .addVertex(new Point(47.71345768748889, 134.769287109375))
                .addVertex(new Point(47.42437092240519, 134.47265625))
                .addVertex(new Point(45.02695045318546, 132.978515625))
                .addVertex(new Point(45.27488643704894, 131.85791015625))
                .addVertex(new Point(44.74673324024678, 130.93505859375))
                .addVertex(new Point(43.8503744993026, 131.2646484375))
                .addVertex(new Point(42.90816007196054, 131.06689453125))
                .addVertex(new Point(42.71473218539458, 130.4022216796875))
                .addVertex(new Point(42.5733097370664, 130.616455078125))
                .addVertex(new Point(42.289500730904564, 130.7208251953125))
                .addVertex(new Point(42.71876810260635, 130.2703857421875))
                .addVertex(new Point(42.90011265525328, 130.2593994140625))
                .addVertex(new Point(42.984558134256055, 129.891357421875))
                .addVertex(new Point(42.44372793752476, 129.715576171875))
                .addVertex(new Point(42.02481360781777, 128.84765625))
                .addVertex(new Point(42.02481360781777, 128.0401611328125))
                .addVertex(new Point(41.492120839687786, 128.2763671875))
                .addVertex(new Point(41.393294288784865, 128.1005859375))
                .addVertex(new Point(41.549700145132725, 127.122802734375))
                .addVertex(new Point(41.80407814427237, 126.89208984375))
                .addVertex(new Point(41.16211393939692, 126.2548828125))
                .addVertex(new Point(40.2155868104582, 124.5025634765625))
                .addVertex(new Point(40.0517964064166, 124.33914184570312))
                .addVertex(new Point(39.969753220824714, 124.35012817382812))
                .addVertex(new Point(39.829631721333726, 124.16473388671875))
                .addVertex(new Point(36.949891786813296, 123.123779296875))
                .addVertex(new Point(26.902476886279807, 122.8271484375))
                .addVertex(new Point(19.228176737766262, 113.4228515625))
                .addVertex(new Point(17.95783210227242, 109.9951171875))
                .addVertex(new Point(17.900341634875257, 108.292236328125))
                .build();

        Point point = new Point(lat, lon);
        return polygon.contains(point);
    }

    public static boolean inHongKong(double lat, double lon) {
        Polygon polygon = Polygon.Builder()
                .addVertex(new Point(22.418010981114797, 113.8623046875))
                .addVertex(new Point(22.506213607257706, 114.03533935546875))
                .addVertex(new Point(22.502724642478643, 114.04692649841309))
                .addVertex(new Point(22.501852387530874, 114.05216217041016))
                .addVertex(new Point(22.50304182473213, 114.0542221069336))
                .addVertex(new Point(22.505024197335548, 114.05696868896484))
                .addVertex(new Point(22.50716512782953, 114.05765533447266))
                .addVertex(new Point(22.51192263246886, 114.0585994720459))
                .addVertex(new Point(22.51398416697013, 114.06057357788086))
                .addVertex(new Point(22.515649230084094, 114.06349182128906))
                .addVertex(new Point(22.51652139799592, 114.0655517578125))
                .addVertex(new Point(22.51675926101668, 114.0681266784668))
                .addVertex(new Point(22.517710709006046, 114.07087326049805))
                .addVertex(new Point(22.51786928303412, 114.07267570495605))
                .addVertex(new Point(22.517036767356853, 114.07404899597168))
                .addVertex(new Point(22.515411365153003, 114.07546520233154))
                .addVertex(new Point(22.514975278382796, 114.07705307006836))
                .addVertex(new Point(22.51810714373511, 114.08215999603271))
                .addVertex(new Point(22.523221049779828, 114.08537864685059))
                .addVertex(new Point(22.524370661516432, 114.08482074737549))
                .addVertex(new Point(22.525520263687213, 114.08310413360596))
                .addVertex(new Point(22.5265905743503, 114.08100128173828))
                .addVertex(new Point(22.52853296881724, 114.0788984298706))
                .addVertex(new Point(22.529484335733734, 114.07851219177246))
                .addVertex(new Point(22.53023749656206, 114.0794563293457))
                .addVertex(new Point(22.531109572390285, 114.08203125))
                .addVertex(new Point(22.532060921559143, 114.08555030822754))
                .addVertex(new Point(22.534042877945797, 114.08722400665283))
                .addVertex(new Point(22.5363419117177, 114.08911228179932))
                .addVertex(new Point(22.537095035146923, 114.09130096435547))
                .addVertex(new Point(22.535985167606935, 114.0941333770752))
                .addVertex(new Point(22.53451854324598, 114.09516334533691))
                .addVertex(new Point(22.5336861278954, 114.09675121307373))
                .addVertex(new Point(22.53392396136474, 114.09902572631836))
                .addVertex(new Point(22.534320349570024, 114.10035610198975))
                .addVertex(new Point(22.53489511044661, 114.1031563282013))
                .addVertex(new Point(22.531575337906343, 114.10253405570984))
                .addVertex(new Point(22.531357320200758, 114.10284519195557))
                .addVertex(new Point(22.53139695980919, 114.10358548164368))
                .addVertex(new Point(22.53230866766302, 114.1044008731842))
                .addVertex(new Point(22.53334919644604, 114.10467982292175))
                .addVertex(new Point(22.534548272272836, 114.10480856895447))
                .addVertex(new Point(22.534340168950425, 114.10577416419983))
                .addVertex(new Point(22.534171704126376, 114.10673975944519))
                .addVertex(new Point(22.534508633568933, 114.10770535469055))
                .addVertex(new Point(22.535350953578682, 114.10825252532959))
                .addVertex(new Point(22.53639145943797, 114.10853147506714))
                .addVertex(new Point(22.53650046435998, 114.10886406898499))
                .addVertex(new Point(22.536332002171516, 114.10931468009949))
                .addVertex(new Point(22.535826614372972, 114.10964727401733))
                .addVertex(new Point(22.533250098804476, 114.1096580028534))
                .addVertex(new Point(22.530921283757117, 114.10956144332886))
                .addVertex(new Point(22.529672626325844, 114.11147117614746))
                .addVertex(new Point(22.5295140658443, 114.11226511001587))
                .addVertex(new Point(22.53051497583197, 114.11462545394897))
                .addVertex(new Point(22.53131768058096, 114.11575198173523))
                .addVertex(new Point(22.53389423220347, 114.11578416824341))
                .addVertex(new Point(22.534488814212718, 114.11585927009583))
                .addVertex(new Point(22.534796013914423, 114.1160523891449))
                .addVertex(new Point(22.534964477976832, 114.1169536113739))
                .addVertex(new Point(22.534746465621666, 114.11802649497986))
                .addVertex(new Point(22.534359988327974, 114.12090182304382))
                .addVertex(new Point(22.535687880143897, 114.12077307701111))
                .addVertex(new Point(22.53524194774921, 114.12222146987915))
                .addVertex(new Point(22.53560860337898, 114.12254333496094))
                .addVertex(new Point(22.537362591165714, 114.12145972251892))
                .addVertex(new Point(22.53773915061013, 114.1214919090271))
                .addVertex(new Point(22.539146284707535, 114.12413120269775))
                .addVertex(new Point(22.539939029995747, 114.12522554397583))
                .addVertex(new Point(22.540880409112944, 114.12565469741821))
                .addVertex(new Point(22.541098411782535, 114.12634134292603))
                .addVertex(new Point(22.540335400932783, 114.12579417228699))
                .addVertex(new Point(22.53974084410046, 114.1260838508606))
                .addVertex(new Point(22.53926519679097, 114.126877784729))
                .addVertex(new Point(22.54032549167322, 114.12888407707214))
                .addVertex(new Point(22.542029873856322, 114.12957072257996))
                .addVertex(new Point(22.541509643153837, 114.13110494613647))
                .addVertex(new Point(22.54143532432201, 114.13436651229858))
                .addVertex(new Point(22.543575690660855, 114.1375207901001))
                .addVertex(new Point(22.542664057203684, 114.1414475440979))
                .addVertex(new Point(22.541891145860703, 114.14226293563843))
                .addVertex(new Point(22.542069510400886, 114.14423704147339))
                .addVertex(new Point(22.540880409112944, 114.14462327957153))
                .addVertex(new Point(22.541217322184476, 114.14679050445557))
                .addVertex(new Point(22.542089328668904, 114.14867877960205))
                .addVertex(new Point(22.54347660035934, 114.14842128753662))
                .addVertex(new Point(22.546290737243336, 114.15091037750244))
                .addVertex(new Point(22.546766360343852, 114.15211200714111))
                .addVertex(new Point(22.54819321981021, 114.15151119232178))
                .addVertex(new Point(22.547083441500213, 114.15026664733887))
                .addVertex(new Point(22.54827248934799, 114.14949417114258))
                .addVertex(new Point(22.5508090905135, 114.15043830871582))
                .addVertex(new Point(22.550769456478903, 114.15146827697754))
                .addVertex(new Point(22.554693170674014, 114.15202617645264))
                .addVertex(new Point(22.554534638930445, 114.1563606262207))
                .addVertex(new Point(22.56087576656133, 114.15966510772705))
                .addVertex(new Point(22.56191607997467, 114.16166067123413))
                .addVertex(new Point(22.559191432968174, 114.16349530220032))
                .addVertex(new Point(22.55921124877709, 114.1653835773468))
                .addVertex(new Point(22.560370468643292, 114.16635990142822))
                .addVertex(new Point(22.56119281529012, 114.1680657863617))
                .addVertex(new Point(22.560687518533733, 114.1695785522461))
                .addVertex(new Point(22.559548117092938, 114.17125225067139))
                .addVertex(new Point(22.559835445300383, 114.17468547821045))
                .addVertex(new Point(22.560162404256566, 114.17614459991455))
                .addVertex(new Point(22.558567233530223, 114.17750716209412))
                .addVertex(new Point(22.555584908336968, 114.1778826713562))
                .addVertex(new Point(22.554138308774494, 114.18107986450195))
                .addVertex(new Point(22.55540656126555, 114.18189525604248))
                .addVertex(new Point(22.554613904825, 114.1869592666626))
                .addVertex(new Point(22.555683989943713, 114.18760299682617))
                .addVertex(new Point(22.5543364739948, 114.19082164764404))
                .addVertex(new Point(22.556001050606962, 114.19228076934814))
                .addVertex(new Point(22.555366928551685, 114.19416904449463))
                .addVertex(new Point(22.555882152943635, 114.19597148895264))
                .addVertex(new Point(22.557229653793655, 114.19622898101807))
                .addVertex(new Point(22.556714434434742, 114.1994047164917))
                .addVertex(new Point(22.557447630635398, 114.20122861862183))
                .addVertex(new Point(22.556100131914786, 114.20180797576904))
                .addVertex(new Point(22.556278478089503, 114.21116352081299))
                .addVertex(new Point(22.55493592205328, 114.21312153339386))
                .addVertex(new Point(22.555961418063905, 114.21805143356323))
                .addVertex(new Point(22.551918838859997, 114.22131299972534))
                .addVertex(new Point(22.550947809544944, 114.22352313995361))
                .addVertex(new Point(22.550967626538053, 114.22438144683838))
                .addVertex(new Point(22.548173402418648, 114.22648429870605))
                .addVertex(new Point(22.547737419084328, 114.22719240188599))
                .addVertex(new Point(22.54714289413592, 114.22727823257446))
                .addVertex(new Point(22.54551784620897, 114.22573328018188))
                .addVertex(new Point(22.54407114110134, 114.22618389129639))
                .addVertex(new Point(22.543952233157825, 114.23377990722656))
                .addVertex(new Point(22.552671877297332, 114.24631118774414))
                .addVertex(new Point(22.560439823369098, 114.2991828918457))
                .addVertex(new Point(22.55219627455193, 114.32836532592773))
                .addVertex(new Point(22.49923558968306, 114.37265396118164))
                .addVertex(new Point(22.439590909172647, 114.40423965454102))
                .addVertex(new Point(22.411663305112103, 114.41093444824219))
                .addVertex(new Point(22.142891812794975, 114.41162109375))
                .addVertex(new Point(22.12826298048241, 113.97903442382812))
                .addVertex(new Point(22.157519125359876, 113.85749816894531))
                .addVertex(new Point(22.186769188065732, 113.81561279296875))
                .addVertex(new Point(22.416900158754242, 113.86213302612305))
                .addVertex(new Point(22.417951472999274, 113.86226177215576))
                .build();

        Point point = new Point(lat, lon);
        return polygon.contains(point);
    }

    public static boolean inMakao(double lat, double lon) {
        Polygon polygon = Polygon.Builder()
                .addVertex(new Point(22.106714488815552, 113.5477352142334))
                .addVertex(new Point(22.14543580243242, 113.54945182800293))
                .addVertex(new Point(22.154736873205902, 113.54146957397461))
                .addVertex(new Point(22.170157806670527, 113.53134155273438))
                .addVertex(new Point(22.18525914361787, 113.5272216796875))
                .addVertex(new Point(22.204729524459104, 113.5338306427002))
                .addVertex(new Point(22.21196069252644, 113.53331565856934))
                .addVertex(new Point(22.2124771902732, 113.53346586227417))
                .addVertex(new Point(22.21356977539565, 113.53589057922363))
                .addVertex(new Point(22.21356977539565, 113.53797197341919))
                .addVertex(new Point(22.21289436014222, 113.5411262512207))
                .addVertex(new Point(22.2130334164896, 113.54164123535156))
                .addVertex(new Point(22.21639059218902, 113.54346513748169))
                .addVertex(new Point(22.216966667517468, 113.54432344436646))
                .addVertex(new Point(22.21664883314604, 113.54767084121704))
                .addVertex(new Point(22.216907073627677, 113.54992389678955))
                .addVertex(new Point(22.217061024457866, 113.55081975460052))
                .addVertex(new Point(22.21566552849935, 113.5508143901825))
                .addVertex(new Point(22.21355984284196, 113.55631828308105))
                .addVertex(new Point(22.211464058284687, 113.56281995773315))
                .addVertex(new Point(22.166739910557208, 113.58919143676758))
                .addVertex(new Point(22.13494154893242, 113.60103607177734))
                .addVertex(new Point(22.110610954462913, 113.57940673828125))
                .addVertex(new Point(22.106684668510347, 113.54783177375793))
                .build();

        Point point = new Point(lat, lon);
        return polygon.contains(point);
    }

    private static double transformLat(double x, double y) {
        double ret = -MN_100 + MN_2 * x + MN_3 * y + MN_0_2 * y * y + MN_0_1 * x * y + MN_0_2 * Math.sqrt(Math.abs(x));
        ret += (MN_20 * Math.sin(MN_6 * x * PI) + MN_20 * Math.sin(MN_2 * x * PI)) * MN_2 / MN_3;
        ret += (MN_20 * Math.sin(y * PI) + MN_40 * Math.sin(y / MN_3 * PI)) * MN_2 / MN_3;
        ret += (MN_160 * Math.sin(y / MN_12 * PI) + MN_320 * Math.sin(y * PI / MN_30)) * MN_2 / MN_3;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = MN_300 + x + MN_2 * y + MN_0_1 * x * x + MN_0_1 * x * y + MN_0_1 * Math.sqrt(Math.abs(x));
        ret += (MN_20 * Math.sin(MN_6 * x * PI) + MN_20 * Math.sin(MN_2 * x * PI)) * MN_2 / MN_3;
        ret += (MN_20 * Math.sin(x * PI) + MN_40 * Math.sin(x / MN_3 * PI)) * MN_2 / MN_3;
        ret += (MN_150 * Math.sin(x / MN_12 * PI) + MN_300 * Math.sin(x / MN_30 * PI)) * MN_2 / MN_3;
        return ret;
    }
}
