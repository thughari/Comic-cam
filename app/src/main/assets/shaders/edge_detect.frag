precision mediump float;
uniform sampler2D uTexture;
uniform vec2 uResolution;
varying vec2 vTexCoord;
float luma(vec3 c){ return dot(c, vec3(.299,.587,.114)); }
void main(){
 vec2 px = 1.0 / uResolution;
 float gx = luma(texture2D(uTexture, vTexCoord + vec2(px.x,0.)).rgb) - luma(texture2D(uTexture, vTexCoord - vec2(px.x,0.)).rgb);
 float gy = luma(texture2D(uTexture, vTexCoord + vec2(0.,px.y)).rgb) - luma(texture2D(uTexture, vTexCoord - vec2(0.,px.y)).rgb);
 float e = smoothstep(.08,.25,length(vec2(gx,gy)));
 vec3 base = texture2D(uTexture, vTexCoord).rgb;
 gl_FragColor = vec4(mix(base, vec3(0.), e), 1.0);
}
