package com.shshcom.module_base.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * desc:
 * author: zhhli
 * 2020/6/26
 */
class ProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private final ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return null;
    }

    private Source source(BufferedSource source) {
        return new ForwardingSource(source) {
            long readTotal = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);

                // Returns the number of bytes read, or -1 if this source is exhausted.
                readTotal += bytesRead != -1 ? bytesRead : 0;

                if (progressListener != null) {
                    int pro = (int) ((100 * readTotal) / contentLength());
                    progressListener.onProgress(pro);
                }

                return bytesRead;

            }
        };

    }


    interface ProgressListener {
        void onProgress(int progress);
    }
}
