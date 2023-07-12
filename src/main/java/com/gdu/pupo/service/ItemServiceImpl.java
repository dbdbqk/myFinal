package com.gdu.pupo.service;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.ItemDTO;
import com.gdu.pupo.domain.ItemImgDTO;
import com.gdu.pupo.domain.ItemImgDetailDTO;
import com.gdu.pupo.mapper.ItemMapper;
import com.gdu.pupo.util.MyFileUtil;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;


@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
  
  private final ItemMapper itemMapper;
  private final MyFileUtil myFileUtil;
  
  @Override
  public List<ItemDTO> getAllItems() {
    return itemMapper.getAllItems();
  }
  
  @Override
  public List<ItemDTO> getItemsByCategoryId(int categoryId) {
    return itemMapper.getItemsByCategoryId(categoryId);
  }
  
  @Transactional
  @Override
  public int insertItem(MultipartHttpServletRequest multipartRequest) {
    
    String itemName = multipartRequest.getParameter("itemName");
    String description= multipartRequest.getParameter("description");
    int price = Integer.parseInt(multipartRequest.getParameter("price"));
    int stock = Integer.parseInt(multipartRequest.getParameter("stock"));
    int categoryId = Integer.parseInt(multipartRequest.getParameter("categoryId"));
    
    ItemDTO itemDTO = new ItemDTO();
    itemDTO.setItemName(itemName);
    itemDTO.setDescription(description);
    itemDTO.setPrice(price);
    itemDTO.setStock(stock);
    itemDTO.setCategoryId(categoryId);
    
    int registerResult = itemMapper.insertItem(itemDTO);
    
    List<MultipartFile> files = multipartRequest.getFiles("files");
    for(MultipartFile multipartFile : files) {
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        try {
          String path = myFileUtil.getPath();
          File dir = new File(path);
          if(dir.exists() == false) {
            dir.mkdirs();
          }
          String originName = multipartFile.getOriginalFilename();
          originName = originName.substring(originName.lastIndexOf("\\") + 1);
          String filesystemName = myFileUtil.getFilesystemName(originName);
          File file = new File(dir, filesystemName);
          multipartFile.transferTo(file);
          String contentType = Files.probeContentType(file.toPath());
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          if(hasThumbnail) {
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file).size(50, 50).toFile(thumbnail);
          }
          ItemImgDTO itemImgDTO = new ItemImgDTO();
          itemImgDTO.setPath(path);
          itemImgDTO.setOriginName(originName);
          itemImgDTO.setFilesystemName(filesystemName);
          itemImgDTO.setHasThumbnail(registerResult);
          itemImgDTO.setItemId(itemDTO.getItemId());
          itemMapper.insertImg(itemImgDTO);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    List<MultipartFile> detailFiles = multipartRequest.getFiles("detailFiles");
    for(MultipartFile multipartFile : detailFiles) {
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        try {
          String path = myFileUtil.getPath();
          File dir = new File(path);
          if(dir.exists() == false) {
            dir.mkdirs();
          }
          String originName = multipartFile.getOriginalFilename();
          originName = originName.substring(originName.lastIndexOf("\\") + 1);
          String filesystemName = myFileUtil.getFilesystemName(originName);
          File file = new File(dir, filesystemName);
          multipartFile.transferTo(file);
          String contentType = Files.probeContentType(file.toPath());
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          if(hasThumbnail) {
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file).size(50, 50).toFile(thumbnail);
          }
          ItemImgDetailDTO itemImgDetailDTO = new ItemImgDetailDTO();
          itemImgDetailDTO.setPathDetail(path);
          itemImgDetailDTO.setOriginDetailName(originName);
          itemImgDetailDTO.setFilesystemDetailName(filesystemName);
          itemImgDetailDTO.setHasDetailThumbnail(registerResult);
          itemImgDetailDTO.setItemId(itemDTO.getItemId());
          itemMapper.insertDetailImg(itemImgDetailDTO);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    return registerResult;
  }
  
  public void getItem(int itemId, Model model) {
    model.addAttribute("item", itemMapper.getItem(itemId));
    model.addAttribute("img", itemMapper.getImg(itemId));
    model.addAttribute("imgDetail", itemMapper.getDetailImg(itemId));
     
  };
  
  
  @Override
  public int itemUpdate(MultipartHttpServletRequest multipartRequest) {
    int itemId = Integer.parseInt(multipartRequest.getParameter("itemId"));
    String itemName = multipartRequest.getParameter("itemName");
    String description = multipartRequest.getParameter("description");
    int price = Integer.parseInt(multipartRequest.getParameter("price"));
    int stock = Integer.parseInt(multipartRequest.getParameter("stock"));
    int categoryId = Integer.parseInt(multipartRequest.getParameter("categoryId"));
    
    ItemDTO itemDTO = new ItemDTO();
    itemDTO.setItemId(itemId);
    itemDTO.setItemName(itemName);
    itemDTO.setDescription(description);
    itemDTO.setPrice(price);
    itemDTO.setStock(stock);
    itemDTO.setCategoryId(categoryId);
    
    int updateResult = itemMapper.updateItem(itemDTO);
    
    List<MultipartFile> files = multipartRequest.getFiles("files");
    for(MultipartFile multipartFile : files) {
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        try {
          String path = myFileUtil.getPath();
          File dir = new File(path);
          if(dir.exists() == false) {
            dir.mkdirs();
          }
          String originName = multipartFile.getOriginalFilename();
          originName = originName.substring(originName.lastIndexOf("\\") + 1);
          String filesystemName = myFileUtil.getFilesystemName(originName);
          File file = new File(dir, filesystemName);
          multipartFile.transferTo(file);
          String contentType = Files.probeContentType(file.toPath());
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          if(hasThumbnail) {
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file).size(50, 50).toFile(thumbnail);
          }
          ItemImgDTO itemImgDTO = new ItemImgDTO();
          itemImgDTO.setPath(path);
          itemImgDTO.setOriginName(originName);
          itemImgDTO.setFilesystemName(filesystemName);
          itemImgDTO.setHasThumbnail(updateResult);
          itemImgDTO.setItemId(itemDTO.getItemId());
          itemMapper.insertImg(itemImgDTO);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    
    List<MultipartFile> detailFiles = multipartRequest.getFiles("detailFiles");
    for(MultipartFile multipartFile : detailFiles) {
      if(multipartFile != null && multipartFile.isEmpty() == false) {
        try {
          String path = myFileUtil.getPath();
          File dir = new File(path);
          if(dir.exists() == false) {
            dir.mkdirs();
          }
          String originName = multipartFile.getOriginalFilename();
          originName = originName.substring(originName.lastIndexOf("\\") + 1);
          String filesystemName = myFileUtil.getFilesystemName(originName);
          File file = new File(dir, filesystemName);
          multipartFile.transferTo(file);
          String contentType = Files.probeContentType(file.toPath());
          boolean hasThumbnail = contentType != null && contentType.startsWith("image");
          if(hasThumbnail) {
            File thumbnail = new File(dir, "s_" + filesystemName);
            Thumbnails.of(file).size(50, 50).toFile(thumbnail);
          }
          ItemImgDetailDTO itemImgDetailDTO = new ItemImgDetailDTO();
          itemImgDetailDTO.setPathDetail(path);
          itemImgDetailDTO.setOriginDetailName(originName);
          itemImgDetailDTO.setFilesystemDetailName(filesystemName);
          itemImgDetailDTO.setHasDetailThumbnail(updateResult);
          itemImgDetailDTO.setItemId(itemDTO.getItemId());
          itemMapper.insertDetailImg(itemImgDetailDTO);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return updateResult;
  }
  
  @Override
  public int itemDelete(int itemId) {
      int deleteResult = itemMapper.deleteItem(itemId);
    return deleteResult;
  }
  
  
  @Override
  public ResponseEntity<byte[]> itemImgDisplay(int itemId) {
    ItemImgDTO itemImgDTO = itemMapper.getImg(itemId);
    ResponseEntity<byte[]> image = null;
    
    try {
      File thumbnail = new File(itemImgDTO.getPath(), itemImgDTO.getFilesystemName());
      image = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(thumbnail), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }
  
  @Override
  public ResponseEntity<byte[]> itemImgDetailDisplay(int itemId) {
    ItemImgDetailDTO itemImgDetailDTO = itemMapper.getDetailImg(itemId);
    ResponseEntity<byte[]> image = null;
    try {
      File thumbnail = new File(itemImgDetailDTO.getPathDetail(), itemImgDetailDTO.getFilesystemDetailName());
      image = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(thumbnail), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return image;
  }
  
  
  
  
}