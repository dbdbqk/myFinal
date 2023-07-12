package com.gdu.pupo.service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gdu.pupo.domain.RegularCategoryDTO;
import com.gdu.pupo.domain.RegularDetailImgDTO;
import com.gdu.pupo.domain.RegularMainImgDTO;
import com.gdu.pupo.domain.RegularProductDTO;
import com.gdu.pupo.domain.RegularPurchaseDTO;
import com.gdu.pupo.domain.UserDTO;
import com.gdu.pupo.mapper.AdminMapper;
import com.gdu.pupo.mapper.RegularMapper;
import com.gdu.pupo.util.MyFileUtil;
import com.gdu.pupo.util.PageUtil;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

	// field
	private final AdminMapper adminMapper;
	private final RegularMapper regularMapper;
	private final PageUtil pageUtil;
	private final MyFileUtil myFileUtil;

	// 회원 조회
//	@Override
//	public List<UserDTO> userList() {
//	
//	    List<UserDTO> selectUserByUserListDTO = adminMapper.selectUserByUserListDTO();
//			return selectUserByUserListDTO;
//		}

	// 회원 count
//	@Override
//	public Map<String, Object> selectUserCount() {
//		return adminMapper.selectUserCount();
//	}
//	
	@Override
	public int sellerCount() {
		return adminMapper.selectSellerCount();
	}

	@Override
	public int userCount() {
		return adminMapper.selectUserCount();
	}

	// 페이지네이션
	@Override
	public void getlistUsingPagination(HttpServletRequest request, Model model) {
		// 파라미터 page가 전달되지 않는 경우 page=1로 처리한다.
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt1.orElse("1"));

		// 전체 레코드 개수를 구한다.
		int totalRecord = adminMapper.getListCount();

		// 세션에 있는 recordPerPage를 가져온다. 세션에 없는 경우 recordPerPage=10으로 처리한다.
		int recordPerPage = 10;

		// 파라미터 order가 전달되지 않는 경우 order=ASC로 처리한다.
		Optional<String> opt3 = Optional.ofNullable(request.getParameter("order"));
		String order = opt3.orElse("ASC");

		// 파라미터 column이 전달되지 않는 경우 column=EMPLOYEE_ID로 처리한다.
		Optional<String> opt4 = Optional.ofNullable(request.getParameter("column"));
		String column = opt4.orElse("USER_NO");

		// PageUtil(Pagination에 필요한 모든 정보) 계산하기
		pageUtil.setPageUtil(page, totalRecord, recordPerPage);

		// DB로 보낼 Map 만들기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("begin", pageUtil.getBegin());
		map.put("recordPerPage", recordPerPage);
//		map.put("order", order);
//		map.put("column", column);
		// begin ~ end 사이의 목록 가져오기
		List<RegularPurchaseDTO> user = adminMapper.getlistUsingPagination(map);
	
		// pagination.jsp로 전달할(forward)할 정보 저장하기
		model.addAttribute("user", user);
		model.addAttribute("pagination", pageUtil.getPagination(
				request.getContextPath() + "/admin/adminUserList.html"));
		model.addAttribute("beginNo", pageUtil.getBegin());
		switch (order) {
		case "ASC":
			model.addAttribute("order", "DESC");
			break; // 현재 ASC 정렬이므로 다음 정렬은 DESC이라고 Jsp에 알려준다.
		case "DESC":
			model.addAttribute("order", "ASC");
			break;
		}
		model.addAttribute("page", page);
	}

	@Override
	public void getRegularListPagination(HttpServletRequest request, Model model) {
		// 파라미터 page가 전달되지 않는 경우 page=1로 처리한다.
		Optional<String> opt1 = Optional.ofNullable(request.getParameter("page"));
		int page = Integer.parseInt(opt1.orElse("1"));

		// 전체 레코드 개수를 구한다.
		int totalRecord = adminMapper.getListCountRegular();

		// 세션에 있는 recordPerPage를 가져온다. 세션에 없는 경우 recordPerPage=10으로 처리한다.
		HttpSession session = request.getSession();
		Optional<Object> opt2 = Optional.ofNullable(session.getAttribute("recordPerPage"));
		int recordPerPage = (int) (opt2.orElse(1));

		// 파라미터 order가 전달되지 않는 경우 order=ASC로 처리한다.
		Optional<String> opt3 = Optional.ofNullable(request.getParameter("order"));
		String order = opt3.orElse("ASC");

		// 파라미터 column이 전달되지 않는 경우 column=EMPLOYEE_ID로 처리한다.
		Optional<String> opt4 = Optional.ofNullable(request.getParameter("column"));
		String column = opt4.orElse("REGULAR_NO");

		// PageUtil(Pagination에 필요한 모든 정보) 계산하기
		pageUtil.setPageUtil(page, totalRecord, recordPerPage);

		// DB로 보낼 Map 만들기
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("begin", pageUtil.getBegin());
		map.put("end", pageUtil.getEnd());
//		map.put("order", order);
//		map.put("column", column);
		// begin ~ end 사이의 목록 가져오기
		List<RegularProductDTO> regularProduct = adminMapper.getRegularListPagination(map);

		// pagination.jsp로 전달할(forward)할 정보 저장하기
		model.addAttribute("regularProduct", regularProduct);
		model.addAttribute("pagination", pageUtil.getPagination(
				request.getContextPath() + "/admin/regularList.html?column=" + column + "&order=" + order));
		model.addAttribute("beginNo", totalRecord - (page - 1) * recordPerPage);
		switch (order) {
		case "ASC":
			model.addAttribute("order", "DESC");
			break; // 현재 ASC 정렬이므로 다음 정렬은 DESC이라고 Jsp에 알려준다.
		case "DESC":
			model.addAttribute("order", "ASC");
			break;
		}
		model.addAttribute("page", page);
		List<RegularCategoryDTO> regularCategoryList = new ArrayList<>();
	 for(RegularProductDTO reg : regularProduct) { 
		 regularCategoryList.add(adminMapper.getRegCategory(reg.getRegularCategory()));
	 }
	 model.addAttribute("regCategory", regularCategoryList);
	}

	// 정기구독상품-삭제
	@Override
	public Map<String, Object> delProduct(int regularNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		RegularProductDTO regularProductDTO = new RegularProductDTO();
		regularProductDTO.setRegularNo(regularNo);
		map.put("delYn", adminMapper.deleteRegularProduct(regularProductDTO));
		return map;
	}

	@Override
	public RegularProductDTO editProduct(int regularNo) {
		RegularProductDTO regularProductDTO = new RegularProductDTO();
		regularProductDTO = adminMapper.selectRegularProduct(regularNo);
		return regularProductDTO;
	}

	@Override
	public List<RegularCategoryDTO> getRegCategory() {
		List<RegularCategoryDTO> list = regularMapper.getRegCategoryList();
		return list;
	}

	// 상품 등록(수정)
	@Transactional
	@Override
	public int editRegular(MultipartHttpServletRequest multipartRequest) { // 상품등록
		// regularProductDTO에 저장 할 파라미터
		int regularNo = Integer.parseInt(multipartRequest.getParameter("regularNo"));
		String regularName = multipartRequest.getParameter("regularName");
		int regularSellPrice = Integer.parseInt(multipartRequest.getParameter("regularSellPrice"));
		int regularOriginPrice = Integer.parseInt(multipartRequest.getParameter("regularOriginPrice"));
		int regularDisplay = Integer.parseInt(multipartRequest.getParameter("regularDisplay"));
		int regularCategory = Integer.parseInt(multipartRequest.getParameter("regularCategory"));
		int regularState = Integer.parseInt(multipartRequest.getParameter("regularState"));
		String regularSimpleDetail = multipartRequest.getParameter("regularSimpleDetail");

		RegularProductDTO regularProductDTO = new RegularProductDTO();
		regularProductDTO.setRegularNo(regularNo);
		regularProductDTO.setRegularCategory(regularCategory);
		regularProductDTO.setRegularDisplay(regularDisplay);
		regularProductDTO.setRegularName(regularName);
		regularProductDTO.setRegularOriginPrice(regularOriginPrice);
		regularProductDTO.setRegularSellPrice(regularSellPrice);
		regularProductDTO.setRegularSimpleDetail(regularSimpleDetail);
		regularProductDTO.setRegularState(regularState);

		int addResult = adminMapper.editRegular(regularProductDTO);

		/* regularDetailImg 테이블에 regularDetailImg 넣기 */

		// 첨부된 파일 목록
		List<MultipartFile> files = multipartRequest.getFiles("files"); // <input type="file" name="files">

		// 첨부가 없는 경우에도 files 리스트는 비어 있지 않고,
		// [MultipartFile[field="files", filename=,
		// contentType=application/octet-stream, size=0]] 형식으로 MultipartFile을 하나 가진 것으로
		// 처리된다.

		// 첨부된 파일 목록 순회
		for (MultipartFile multipartFile : files) {
			// 첨부된 파일이 있는지 체크
			if (multipartFile != null && multipartFile.isEmpty() == false) {
				// 예외 처리
				try {
					/* HDD에 첨부 파일 저장하기 */
					// 첨부 파일의 저장 경로
					String regDetailimgName = myFileUtil.getPath();

					// 첨부 파일의 저장 경로가 없으면 만들기
					File dir = new File(regDetailimgName);
					if (dir.exists() == false) {
						dir.mkdirs();
					}
					// 첨부 파일의 원래 이름
					String originName = multipartFile.getOriginalFilename();
					originName = originName.substring(originName.lastIndexOf("\\") + 1); // IE는 전체 경로가 오기 때문에 마지막 역슬래시																							// 뒤에 있는 파일명만 사용한다.

					// 첨부 파일의 저장 이름
					String filesystemName = myFileUtil.getFilesystemName(originName);

					// 첨부 파일의 File 객체 (HDD에 저장할 첨부 파일)
					File file = new File(dir, filesystemName);

					// 첨부 파일을 HDD에 저장
					multipartFile.transferTo(file); // 실제로 서버에 저장된다.

					/* 썸네일(첨부 파일이 이미지인 경우에만 썸네일이 가능) */

					// 첨부 파일의 Content-Type 확인
					String contentType = Files.probeContentType(file.toPath()); // 이미지 파일의 Content-Type : image/jpeg,
																				// image/png, image/gif, ...
					// DB에 저장할 썸네일 유무 정보 처리
					boolean hasThumbnail = contentType != null && contentType.startsWith("image");

					// 첨부 파일의 Content-Type이 이미지로 확인되면 썸네일을 만듬
					if (hasThumbnail) {

						// HDD에 썸네일 저장하기 (thumbnailator 디펜던시 사용)
						File thumbnail = new File(dir, "s_" + filesystemName);
						Thumbnails.of(file).size(50, 50).toFile(thumbnail);
					}

					/* DB에 첨부 파일 정보 저장하기 */

					// DB로 보낼 AttachDTO 만들기
					RegularDetailImgDTO regularDetailImgDTO = new RegularDetailImgDTO();
					regularDetailImgDTO.setRegDetailImgName(regDetailimgName);
					regularDetailImgDTO.setRegularNo(regularProductDTO.getRegularNo());
					regularDetailImgDTO.setRegFilesystemName(filesystemName);
					// DB로 AttachDTO 보내기
					adminMapper.editRegImg(regularDetailImgDTO);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		/* regularDetailImg 테이블에 regularDetailImg 넣기 */

		// 첨부된 파일 목록
		List<MultipartFile> mainImg = multipartRequest.getFiles("mainImg"); // <input type="file" name="files">

		// 첨부된 파일 목록 순회
		for (MultipartFile multipartFile : mainImg) {

			// 첨부된 파일이 있는지 체크
			if (multipartFile != null && multipartFile.isEmpty() == false) {

				// 예외 처리
				try {

					/* HDD에 첨부 파일 저장하기 */

					// 첨부 파일의 저장 경로
					String regMainImgName = myFileUtil.getPath();

					// 첨부 파일의 저장 경로가 없으면 만들기
					File dir = new File(regMainImgName);
					if (dir.exists() == false) {
						dir.mkdirs();
					}

					// 첨부 파일의 원래 이름
					String originName = multipartFile.getOriginalFilename();
					originName = originName.substring(originName.lastIndexOf("\\") + 1); // IE는 전체 경로가 오기 때문에 마지막 역슬래시
																							// 뒤에 있는 파일명만 사용한다.

					// 첨부 파일의 저장 이름
					String filesystemName = myFileUtil.getFilesystemName(originName);

					// 첨부 파일의 File 객체 (HDD에 저장할 첨부 파일)
					File file = new File(dir, filesystemName);

					// 첨부 파일을 HDD에 저장
					multipartFile.transferTo(file); // 실제로 서버에 저장된다.

					/* 썸네일(첨부 파일이 이미지인 경우에만 썸네일이 가능) */

					// 첨부 파일의 Content-Type 확인
					String contentType = Files.probeContentType(file.toPath()); // 이미지 파일의 Content-Type : image/jpeg,
																				// image/png, image/gif, ...

					// DB에 저장할 썸네일 유무 정보 처리
					boolean hasThumbnail = contentType != null && contentType.startsWith("image");

					// 첨부 파일의 Content-Type이 이미지로 확인되면 썸네일을 만듬
					if (hasThumbnail) {

						// HDD에 썸네일 저장하기 (thumbnailator 디펜던시 사용)
						File thumbnail = new File(dir, "s_" + filesystemName);
						Thumbnails.of(file).size(50, 50).toFile(thumbnail);

					}

					/* DB에 첨부 파일 정보 저장하기 */

					// DB로 보낼 AttachDTO 만들기
					RegularMainImgDTO regularMainImgDTO = new RegularMainImgDTO();
					regularMainImgDTO.setRegMainImgName(regMainImgName);
					regularMainImgDTO.setRegFilesystemName(filesystemName);
					regularMainImgDTO.setRegularNo(regularProductDTO.getRegularNo());

					// DB로 AttachDTO 보내기
					adminMapper.editRegMainImg(regularMainImgDTO);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return addResult;
	}

}
