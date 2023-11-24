package com.example.springsehibernate.Service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.dropbox.core.v2.files.DeleteResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class DropboxService {

    @Value("${dropbox.access.token}")
    private String accessToken;

    private DbxClientV2 client;

    private void initClient() {
        if (client == null) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            client = new DbxClientV2(config, accessToken);
        }
    }

    public String uploadFile(MultipartFile file, String dropboxPath) throws DbxException, IOException {
        initClient();
        try (InputStream in = file.getInputStream()) {
            FileMetadata metadata = client.files().uploadBuilder(dropboxPath)
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(in);
            return metadata.getPathLower();
        }
    }

    public OutputStream downloadFile(String dropboxPath) throws DbxException, IOException {
        initClient();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        client.files().downloadBuilder(dropboxPath).download(out);
        return out;
    }

    public Metadata getFileInfo(String dropboxPath) throws DbxException {
        initClient();
        return client.files().getMetadata(dropboxPath);
    }

    public void deleteFile(String dropboxPath) throws DbxException {
        initClient();
        DeleteResult result = client.files().deleteV2(dropboxPath);
    }

    // Phương thức khác để lấy link chia sẻ của file trên Dropbox
    public String createSharedLink(String dropboxPath) throws DbxException {
        initClient();
        return client.sharing().createSharedLinkWithSettings(dropboxPath).getUrl();
    }
}



