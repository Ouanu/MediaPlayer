package com.moment.mobileplayer;

import com.bumptech.glide.module.AppGlideModule;

public class CMAppGlideModule extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
