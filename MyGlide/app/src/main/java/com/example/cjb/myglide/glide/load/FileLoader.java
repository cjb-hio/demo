package com.example.cjb.myglide.glide.load;

import android.net.Uri;

import java.io.File;

public class FileLoader<Data> implements ModelLoader<File, Data> {

    private ModelLoader<Uri, Data> loader;

    public FileLoader(ModelLoader loader) {
        this.loader = loader;
    }

    @Override
    public boolean handles(File file) {
        return true;
    }

    @Override
    public Loaddata<Data> buildData(File file) {
        return loader.buildData(Uri.fromFile(file));
    }

    public static class Factory implements ModelLoader.ModelLoadFactory{

        @Override
        public ModelLoader build(ModelLoaderRegistry registry) {
            return null;
        }
    }
}
