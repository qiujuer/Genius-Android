/*************************************************
Copyright:  Copyright QIUJUER 2015-2017.
Author:		QiuJuer
CreateDate:		2016-12-19
ChangeDate: 	2016-12-19
Description:Call iamge bluring by clip parts
**************************************************/
#include "stackblur.h"
#include "clipblur.h"
#include <string.h>

int *clip_blur_ARGB_8888(int *pix, const int w, const int h,
                         const int radius, const int parts) {
    int radiusSize = w * radius;
    int diameterSize = radiusSize * 2;
    int partsHeight = h / parts;
    int surplusHeight = h % parts;

    size_t sizeOfInt = sizeof(int);
    int *tempR = (int *) malloc(diameterSize * sizeOfInt);
    int *tempE = (int *) malloc(radiusSize * sizeOfInt);

    int *temp = NULL;
    int tempLen;

    for (int i = 0; i < parts; i++) {
        int isNotFirst = i == 0 ? 0 : 1;
        int isNotEnd = i == parts - 1 ? 0 : 1;

        int startIndex = i == 0 ? 0 : partsHeight * w * i - radiusSize;

        temp = &pix[startIndex];
        int onceHeight = (partsHeight + radius * (isNotFirst && isNotEnd ? 2 : 1));
        if (!isNotEnd) {
            onceHeight += surplusHeight;
        }

        tempLen = onceHeight * w;

        if (isNotEnd) {
            memcpy(tempR, &temp[tempLen - diameterSize], sizeOfInt * diameterSize);
        }

        blur_ARGB_8888(temp, w, onceHeight, radius);

        if (isNotFirst) {
            memcpy(temp, tempE, sizeOfInt * radiusSize);
        }
        if (isNotEnd) {
            memcpy(tempE, &temp[tempLen - diameterSize], sizeOfInt * radiusSize);
            memcpy(&temp[tempLen - diameterSize], tempR, sizeOfInt * diameterSize);
        }
    }

    free(tempR);
    free(tempE);
    return (pix);
}

short *clip_blur_RGB_565(short *pix, const int w, const int h,
                         const int radius, const int parts) {
    int radiusSize = w * radius;
    int diameterSize = radiusSize * 2;
    int partsHeight = h / parts;
    int surplusHeight = h % parts;

    size_t sizeOfInt = sizeof(short);
    short *tempR = (short *) malloc(diameterSize * sizeOfInt);
    short *tempE = (short *) malloc(radiusSize * sizeOfInt);

    short *temp = NULL;
    int tempLen;

    for (int i = 0; i < parts; i++) {
        int isNotFirst = i == 0 ? 0 : 1;
        int isNotEnd = i == parts - 1 ? 0 : 1;

        int startIndex = i == 0 ? 0 : partsHeight * w * i - radiusSize;

        temp = &pix[startIndex];
        int onceHeight = (partsHeight + radius * (isNotFirst && isNotEnd ? 2 : 1));
        if (!isNotEnd) {
            onceHeight += surplusHeight;
        }

        tempLen = onceHeight * w;

        if (isNotEnd) {
            memcpy(tempR, &temp[tempLen - diameterSize], sizeOfInt * diameterSize);
        }

        blur_RGB_565(temp, w, onceHeight, radius);

        if (isNotFirst) {
            memcpy(temp, tempE, sizeOfInt * radiusSize);
        }
        if (isNotEnd) {
            memcpy(tempE, &temp[tempLen - diameterSize], sizeOfInt * radiusSize);
            memcpy(&temp[tempLen - diameterSize], tempR, sizeOfInt * diameterSize);
        }
    }

    free(tempR);
    free(tempE);
    return (pix);
}