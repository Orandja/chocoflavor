#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 uv;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, uv);
    if (color.a == 0.0) discard;
    fragColor = vec4(ColorModulator.rgb * vertexColor.rgb, ColorModulator.a);
}