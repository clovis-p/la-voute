export const typeIcons = {
  Folder: 'pi-folder',
  Image: 'pi-image',
  Audio: 'pi-headphones',
  Video: 'pi-video',
  Archive: 'pi-box',
  Document: 'pi-file',
  Program: 'pi-code',
  Other: 'pi-question-circle',
};

export function resolveFileType(file) {
  if (file.isDirectory) {
    return 'Folder';
  }

  const ext = file.name.match(/\.([^.]+)$/)?.[1].toLowerCase() ?? '';
  if (/^(png|jpg|jpeg|gif|webp|svg|bmp|tiff|tif|heic|heif|avif|ico)$/.test(ext)) {
    return 'Image';
  }
  if (/^(mp3|wav|flac|aac|ogg|oga|m4a|wma|opus|aiff|aif)$/.test(ext)) {
    return 'Audio';
  }
  if (/^(mp4|mkv|webm|mov|avi|wmv|flv|m4v|mpg|mpeg|3gp|ogv|mts|m2ts|vob)$/.test(ext)) {
    return 'Video';
  }
  if (/^(zip|tar|gz|tgz|bz2|tbz2|xz|txz|7z|rar|zst|lz|lzma|lzh|cab|iso|ar)$/.test(ext)) {
    return 'Archive';
  }
  if (
    /^(pdf|doc|docx|odt|rtf|txt|md|tex|pages|xls|xlsx|ods|csv|tsv|numbers|ppt|pptx|odp|key|epub|mobi)$/.test(
      ext,
    )
  ) {
    return 'Document';
  }
  if (
    /^(js|mjs|cjs|jsx|ts|tsx|py|java|c|cc|cpp|cxx|h|hpp|cs|go|rs|rb|php|swift|kt|kts|scala|lua|pl|r|dart|sh|bash|zsh|sql|html|htm|css|scss|sass|less|vue|svelte|json|xml|yaml|yml|toml|ini|exe|msi|dmg|app|deb|rpm|apk|appimage)$/.test(
      ext,
    )
  ) {
    return 'Program';
  }

  return 'Other';
}
