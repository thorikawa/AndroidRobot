package com.polysfactory.contactphoto.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 独自のコンテンツプロバイダーのメタ情報クラス
 * @author $Author: horikawa.takahiro@gmail.com $
 * @version $Revision: 52 $
 */
public class ContactPhotoStore {
    /** AUTHORITY */
    public static final String AUTHORITY = "com.polysfactory.contactphoto.provider";

    /** AUTHORITYパス */
    private static final String CONTENT_AUTHORITY_SLASH = "content://" + AUTHORITY + "/";

    /**
     * Downloadメタ情報
     * @author $Author: horikawa.takahiro@gmail.com $
     * @version $Revision: 52 $
     */
    public static final class ContactPhoto implements ContactPhotoColumns {

        /** Content Uri */
        public static final Uri EXTERNAL_CONTENT_URI = Uri.parse(CONTENT_AUTHORITY_SLASH + "contactphoto");

    }

    /**
     * Download Columns
     * @author $Author: horikawa.takahiro@gmail.com $
     * @version $Revision: 52 $
     */
    public static interface ContactPhotoColumns extends BaseColumns {
        /** 連絡先ID */
        String CONTACT_ID = "contact_id";

        /** 画像ファイルURI */
        String IMAGE_URI = "image_uri";

        /** 顔のオフセット位置(x座標) */
        String FACE_OFFSET_X = "face_offset_x";

        /** 顔のオフセット位置(x座標) */
        String FACE_OFFSET_Y = "face_offset_y";

        /** 顔の幅 */
        String FACE_WIDTH = "face_width";

        /** 顔の高さ */
        String FACE_HEIGHT = "face_height";
    }
}
