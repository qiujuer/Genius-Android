/*************************************************
Copyright:  Copyright QIUJUER 2015.
Author:		QiuJuer
CreateDate:		2015-04-28
ChangeDate: 	2015-04-28
Description:Realize image blurred images blurred
**************************************************/
#include <stackblur.h>
#include <malloc.h>

#define ABS(a) ((a)<(0)?(-a):(a))
#define MAX(a,b) ((a)>(b)?(a):(b))
#define MIN(a,b) ((a)<(b)?(a):(b))

/*************************************************
Function:		StackBlur
Description:    Using stack way blurred image pixels
Calls:          malloc
Table Accessed: NULL
Table Updated:	NULL
Input:          Collection of pixels, wide image, image is high, the blur radius
Output:         After return to fuzzy collection of pixels
Return:         After return to fuzzy collection of pixels
Others:         NULL
*************************************************/
int* blur(int* pix, int w, int h, int radius) {
	int wm = w - 1;
	int hm = h - 1;
	int wh = w * h;
	int div = radius + radius + 1;

	int *r = (int *)malloc(wh * sizeof(int));
	int *g = (int *)malloc(wh * sizeof(int));
	int *b = (int *)malloc(wh * sizeof(int));
	int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;

	int *vmin = (int *)malloc(MAX(w, h) * sizeof(int));

	int divsum = (div + 1) >> 1;
	divsum *= divsum;
	int *dv = (int *)malloc(256 * divsum * sizeof(int));
	for (i = 0; i < 256 * divsum; i++) {
		dv[i] = (i / divsum);
	}

	yw = yi = 0;

	int(*stack)[3] = (int(*)[3])malloc(div * 3 * sizeof(int));
	int stackpointer;
	int stackstart;
	int *sir;
	int rbs;
	int r1 = radius + 1;
	int routsum, goutsum, boutsum;
	int rinsum, ginsum, binsum;

	for (y = 0; y < h; y++) {
		rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
		for (i = -radius; i <= radius; i++) {
			p = pix[yi + (MIN(wm, MAX(i, 0)))];
			sir = stack[i + radius];
			sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
			sir[2] = (p & 0x0000ff);

			rbs = r1 - ABS(i);
			rsum += sir[0] * rbs;
			gsum += sir[1] * rbs;
			bsum += sir[2] * rbs;
			if (i > 0) {
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
			}
			else {
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
			}
		}
		stackpointer = radius;

		for (x = 0; x < w; x++) {

			r[yi] = dv[rsum];
			g[yi] = dv[gsum];
			b[yi] = dv[bsum];

			rsum -= routsum;
			gsum -= goutsum;
			bsum -= boutsum;

			stackstart = stackpointer - radius + div;
			sir = stack[stackstart % div];

			routsum -= sir[0];
			goutsum -= sir[1];
			boutsum -= sir[2];

			if (y == 0) {
				vmin[x] = MIN(x + radius + 1, wm);
			}
			p = pix[yw + vmin[x]];

			sir[0] = (p & 0xff0000) >> 16;
			sir[1] = (p & 0x00ff00) >> 8;
			sir[2] = (p & 0x0000ff);

			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];

			rsum += rinsum;
			gsum += ginsum;
			bsum += binsum;

			stackpointer = (stackpointer + 1) % div;
			sir = stack[(stackpointer) % div];

			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];

			rinsum -= sir[0];
			ginsum -= sir[1];
			binsum -= sir[2];

			yi++;
		}
		yw += w;
	}
	for (x = 0; x < w; x++) {
		rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
		yp = -radius * w;
		for (i = -radius; i <= radius; i++) {
			yi = MAX(0, yp) + x;

			sir = stack[i + radius];

			sir[0] = r[yi];
			sir[1] = g[yi];
			sir[2] = b[yi];

			rbs = r1 - ABS(i);

			rsum += r[yi] * rbs;
			gsum += g[yi] * rbs;
			bsum += b[yi] * rbs;

			if (i > 0) {
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
			}
			else {
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
			}

			if (i < hm) {
				yp += w;
			}
		}
		yi = x;
		stackpointer = radius;
		for (y = 0; y < h; y++) {
			// Preserve alpha channel: ( 0xff000000 & pix[yi] )
			pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

			rsum -= routsum;
			gsum -= goutsum;
			bsum -= boutsum;

			stackstart = stackpointer - radius + div;
			sir = stack[stackstart % div];

			routsum -= sir[0];
			goutsum -= sir[1];
			boutsum -= sir[2];

			if (x == 0) {
				vmin[y] = MIN(y + r1, hm) * w;
			}
			p = x + vmin[y];

			sir[0] = r[p];
			sir[1] = g[p];
			sir[2] = b[p];

			rinsum += sir[0];
			ginsum += sir[1];
			binsum += sir[2];

			rsum += rinsum;
			gsum += ginsum;
			bsum += binsum;

			stackpointer = (stackpointer + 1) % div;
			sir = stack[stackpointer];

			routsum += sir[0];
			goutsum += sir[1];
			boutsum += sir[2];

			rinsum -= sir[0];
			ginsum -= sir[1];
			binsum -= sir[2];

			yi += w;
		}
	}

	free(r);
	free(g);
	free(b);
	free(vmin);
	free(dv);
	free(stack);
	return(pix);
}