#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec2 ScreenSize;
uniform float GameTime;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

float _CellSize = 0.1;
int _Seed = int(ScreenSize.x * ScreenSize.y);
vec2 iResolution = vec2(1, 1);
float scaledTime = sqrt(GameTime) * 0.2;

float random(in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

// 2D Noise based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise2(in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Four corners in 2D of a tile
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    // Smooth Interpolation

    // Cubic Hermine Curve.  Same as SmoothStep()
    vec2 u = f * f * (3.0 - 2.0 * f);
    // u = smoothstep(0.,1.,f);

    // Mix 4 coorners percentages
    return mix(a, b, u.x) +
    (c - a) * u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

vec2 grad(ivec2 z) { // replace this anything that returns a random vector
    // 2D to 1D  (feel free to replace by some other)
    int n = z.x + z.y * 11111;

    // Hugo Elias hash (feel free to replace by another one)
    n = (n << 13) ^ n;
    n = (n * (n * n * 15731 + 789221) + 1376312589) >> 16;

    #if 1

    // simple random vectors
    return vec2(cos(float(n) * noise2(vec2(scaledTime))), sin(float(n) * noise2(vec2(scaledTime))));

    #else

    // Perlin style vectors
    n &= 7;
    vec2 gr = vec2(n & 1, n >> 1) * 2.0 - 1.0;
    return (n >= 6) ? vec2(0.0, gr.x) : (n >= 4) ? vec2(gr.x, 0.0) : gr;
    #endif
}

float noise(in vec2 p) {
    ivec2 i = ivec2(floor(p));
    vec2 f = fract(p);

    vec2 u = f * f * (3.0 - 2.0 * f);// feel free to replace by a quintic smoothstep instead

    return mix(
    mix(
    dot(grad(i + ivec2(0, 0)), f - vec2(0.0, 0.0)),
    dot(grad(i + ivec2(1, 0)), f - vec2(1.0, 0.0)),
    u.x
    ),
    mix(
    dot(grad(i + ivec2(0, 1)), f - vec2(0.0, 1.0)),
    dot(grad(i + ivec2(1, 1)), f - vec2(1.0, 1.0)),
    u.x
    ),
    u.y
    );
}


float hash(uint n) {
    // integer hash copied from Hugo Elias
    n = (n << 13U) ^ n;
    n = n * (n * n * 15731UL + 0x789221UL) + 0x1376312589UL;
    return float(n & uint(0x7fffffffU)) / float(0x7fffffff);
}

float hash(int n) {
    return hash(uint(n));
}

float worley(vec3 coord, int axisCellCount) {
    ivec3 cell = ivec3(floor(coord / _CellSize));

    vec3 localSamplePos = vec3(coord / _CellSize - cell);

    float dist = 1.0f;

    for (int x1 = -1; x1 <= 1; ++x1) {
        for (int y1 = -1; y1 <= 1; ++y1) {
            for (int z1 = -1; z1 <= 1; ++z1) {
                ivec3 cellCoordinate = cell + ivec3(x1, y1, z1);
                int x2 = cellCoordinate.x;
                int y2 = cellCoordinate.y;
                int z2 = cellCoordinate.z;

                if (x2 == -1 || x2 == axisCellCount || y2 == -1 || y2 == axisCellCount || z2 == -1 || z2 == axisCellCount) {
                    ivec3 wrappedCellCoordinate = ivec3(mod(cellCoordinate + axisCellCount, ivec3(axisCellCount)));
                    int wrappedCellIndex = wrappedCellCoordinate.x + axisCellCount * (wrappedCellCoordinate.y + wrappedCellCoordinate.z * axisCellCount);
                    vec3 featurePointOffset = cellCoordinate + vec3(hash(_Seed + wrappedCellIndex), hash(_Seed + wrappedCellIndex * 2), hash(_Seed + wrappedCellIndex * 3));
                    dist = min(dist, distance(cell + localSamplePos, featurePointOffset));
                } else {
                    int cellIndex = cellCoordinate.x + axisCellCount * (cellCoordinate.y + cellCoordinate.z * axisCellCount);
                    vec3 featurePointOffset = cellCoordinate + vec3(hash(_Seed + cellIndex), hash(_Seed + cellIndex * 2), hash(_Seed + cellIndex * 3));
                    dist = min(dist, distance(cell + localSamplePos, featurePointOffset));
                }
            }
        }
    }

    dist = sqrt(1.0f - dist * dist);
    dist *= dist * dist * dist * dist * dist;
    return dist;
}

float saturate(float x) {
    return max(0, min(1, x));
}

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor /** ColorModulator*/;
    /*if (color.a < 0.1) {
        discard;
    }*/

    vec2 st = texCoord0.xy / iResolution;
    float dist = 0.0;

    // a. The DISTANCE from the pixel to the center
    //dist = distance(st, vec2(0.5));
    vec2 vp = texCoord0.xy - vec2(0.5);
    dist = min(1.0f, length(vp / 0.5));

    // b. The LENGTH of the vector
    //    from the pixel to the center
    // vec2 toCenter = vec2(0.5)-st;
    // pct = length(toCenter);

    // c. The SQUARE ROOT of the vector
    //    from the pixel to the center
    // vec2 tC = vec2(0.5)-st;
    // pct = sqrt(tC.x*tC.x+tC.y*tC.y);

    int res = 64;
    vec2 uv = /*floor*/((st * vec2(iResolution.x / iResolution.y, 1.0) + 1 * 0.25) * res) / res;

    float f = 0.0;

    uv *= 2.0;
    mat2 m = mat2(1.6, 1.2, -1.2, 1.6);
    f = 0.5000 * noise(uv);
    uv = m * uv;
    f += 0.2500 * noise(uv);
    uv = m * uv;
    f += 0.1250 * noise(uv);
    uv = m * uv;
    f += 0.0625 * noise(uv);
    uv = m * uv;

    f = 0.5 + 0.5 * f;


    //float noise = noise(texCoord0 * 5);

    /*if (texCoord0.x < 0.0025 || texCoord0.x > 1 - 0.0025 || texCoord0.y < 0.0025 || texCoord0.y > 1 - 0.0025) {
        fragColor = vec4(1, 0, 0, 1);
        return;
    }*/

    vec3 col = (1 - vec3(dist) / 2) / 1.5;
    vec3 modulator = vertexColor.rgb;/*vec3(0.9, 0.3, 0.3);*/

    float n = (1 - noise(uv / (8)));

    vec3 render = col * n;
    //render *= (smoothstep(n, 1, dist));
    render *= modulator;

    /*if (dist < 0.25) {
        fragColor = vec4(color * modulator, 1 - dist * 2.5);
        return;
    }*/

    /*if (render.x > 1 && render.y > 1 && render.z > 1) {
        discard;
    }*/


    float w = worley(vec3(st, GameTime * 100), _Seed);

    float dist2 = dist;
    float dist3 = smoothstep(0.75, 1, dist + 0.2f);
    dist = smoothstep(0.75, 1, dist);
    float falloff = min(1.0f, dist + n);
    float nfalloff = min(1.0f, dist3);

    fragColor = vec4(modulator, saturate(dist3) * (1 - falloff));
    fragColor += vec4(0, 0, 0, 1 - nfalloff);

    fragColor *= vec4(vec3(f * 2), 1);


    //fragColor = color;
    //fragColor = vec4(1, 0, 0, 1 - (dist2));


    //fragColor = vec4(color * modulator, (1 - dist * 2.5) * (noise(uv) * (1 - dist * 1) * 8));

    //fragColor = vec4(vec3(f), 1);

    /*vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }

    fragColor = vec4(1, 0, 0, 1);*/
}
