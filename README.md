file-encode
===========

Given a file or directory, filenc.exe will encrypt it (or decrypt it) into a password protected zip file. 
Most useful for files with sensitive information that I place on the cloud (e.g. dropbox)

Usage
=====
Encrypt a file or directory:
filenc.exe e <file> | <dir>

Decrypt a file or directory:
filenc.exe d <file> | <dir>


Add to windows context menu
===========================
import these entries to registry

  Windows Registry Editor Version 5.00
  
  [HKEY_CLASSES_ROOT\*\shell]
  
  [HKEY_CLASSES_ROOT\*\shell\File Decode]
  
  [HKEY_CLASSES_ROOT\*\shell\File Decode\Command]
  @="\"C:\\NTRESKIT\\filenc.exe\" d \"%1\""
  
  [HKEY_CLASSES_ROOT\*\shell\File Encode]
  
  [HKEY_CLASSES_ROOT\*\shell\File Encode\Command]
  @="\"C:\\NTRESKIT\\filenc.exe\" e \"%1\""
