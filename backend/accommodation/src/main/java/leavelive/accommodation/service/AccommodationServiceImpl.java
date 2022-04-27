package leavelive.accommodation.service;

import leavelive.accommodation.domain.AccommodationArticle;
import leavelive.accommodation.domain.AccommodationFav;
import leavelive.accommodation.domain.dto.AccommodationArticleDto;
import leavelive.accommodation.repository.AccommodationFavRepository;
import leavelive.accommodation.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Null;
import java.io.File;
import java.nio.file.FileSystemException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService{
    private final AccommodationRepository repo;
    private final AccommodationFavRepository favRepo;
    @Override
    public List<AccommodationArticleDto> getAllAccommodation() {
        List<AccommodationArticle> entities=repo.findAll();
        //entity를 dto로 변환
        List<AccommodationArticleDto> list=new ArrayList<>();
        for (int i=0; i<entities.size(); i++){
            AccommodationArticleDto dto=new AccommodationArticleDto();
            list.add(dto.of(entities.get(i)));
        }
        return list;
    }

    @Override
    public AccommodationArticleDto getAccommodation(Long id) {
        Optional<AccommodationArticle> entity=repo.findById(id);
        if(!entity.isPresent())throw new NullPointerException("해당하는 숙소가 없습니다.");
        //entity를 dto로 변환
        AccommodationArticleDto dto=new AccommodationArticleDto();
        return dto.of(entity.get());
    }

    @Override
    public Long delete(Long id,String userId) {
        // 내가 작성한 숙소가 맞는지 확인
        Optional<AccommodationArticle> accommodationArticle=repo.findById(id);
        if(!accommodationArticle.isPresent()) throw new NullPointerException("해당하는 숙소가 없습니다.");
        if(!accommodationArticle.get().getUserId().equals(userId)) throw new NullPointerException("자신이 등록한 숙소만 삭제할 수 있습니다.");
        // 연결되어있는거 먼저 삭제
        // id를 가지고 있는 모든 favRepo 찾고, 삭제
        List<AccommodationFav> list=favRepo.findAllByAcommodationId(id);
        if(list!=null) {
            for(AccommodationFav entity:list){
                favRepo.deleteById(entity.getId());
            }
        }
        repo.deleteById(id);
        return id;
    }

    @Override
    public AccommodationArticleDto save(AccommodationArticleDto dto,String userId) {
        dto.setUserId(userId);
        AccommodationArticle entity=new AccommodationArticle();
        repo.save(entity.of(dto));
        return dto;
    }

    @Override
    public AccommodationArticleDto update(AccommodationArticleDto dto,Long id,String userId) {
        Optional<AccommodationArticle> entity=repo.findById(id);
        AccommodationArticle result=new AccommodationArticle();
        if(!entity.isPresent()) throw new NullPointerException("해당하는 숙소가 없습니다.");
        if(!entity.get().getUserId().equals(userId)) throw new NullPointerException("자신이 등록한 숙소만 수정할 수 있습니다.");
        AccommodationArticleDto oriDto=new AccommodationArticleDto(); //현재 entity
        oriDto=oriDto.of(entity.get());
        oriDto.setId(id);
        oriDto.setUserId(userId);
        // 바뀐 부분
        if(dto.getCnt()!=0){
            oriDto.setCnt(dto.getCnt());
        }
        if(dto.getContents()!=null){
            oriDto.setContents(dto.getContents());
        }
        if(dto.getPrice()!=0){
            oriDto.setPrice(dto.getPrice());
        }
        if(dto.getLoc()!=null){
            oriDto.setLoc(dto.getLoc());
        }
        if(dto.getName()!=null){
            oriDto.setName(dto.getName());
        }
        if(dto.getPicPath()!=null){
            oriDto.setPicPath(dto.getPicPath());
        }
        if(dto.getCooking()!=oriDto.getCooking()){
            oriDto.setCooking(dto.getCooking());
        }
        if(dto.getGarden()!=oriDto.getGarden()){
            oriDto.setGarden(dto.getGarden());
        }
        repo.save(result.updateOf(oriDto));
        return dto;
    }

    @Override
    public AccommodationArticleDto saveImage(List<MultipartFile> files,Long id) {
        System.out.println("saveImage 서비스");
        for (MultipartFile file:files){
            System.out.println(file.getOriginalFilename());
        }
        Optional<AccommodationArticle> entity=repo.findById(id);
        if(!entity.isPresent()) throw new NullPointerException("해당하는 숙소가 없습니다.");
        AccommodationArticleDto oriDto=new AccommodationArticleDto(); //현재 entity
        String images="";
        if(files!=null){
            LocalDateTime now= LocalDateTime.now(); //현재 시간 저장
            DateTimeFormatter dateTimeFormatter =
                    DateTimeFormatter.ofPattern("yyyyMMdd");
            String current_date = now.format(dateTimeFormatter);

            String abPath=new File("").getAbsolutePath()+File.separator;
            System.out.println("경로 "+abPath);

            // 세부 경로
            String path="images"+File.separator+current_date;
            File file=new File(path);
            System.out.println("세부 경로 "+path);

            if(!file.exists()){
                boolean success=file.mkdir();
                if(!success){
                    throw new NullPointerException("파일 경로를 생성하지 못했습니다.");
                }
            }
            for (MultipartFile multipartFile:files){
                String originalFileExtension;
                String contentType=multipartFile.getContentType();
                if(ObjectUtils.isEmpty(contentType)){
                    continue;
                }
                if(contentType.contains("image/PNG")){
                    originalFileExtension=".PNG";
                }else if(contentType.contains("image/png")){
                    originalFileExtension=".png";
                }else if(contentType.contains("image/jpeg")){
                    originalFileExtension=".jpeg";
                }else if(contentType.contains("image/JPEG")){
                    originalFileExtension=".JPEG";
                }else{
                    System.out.println("이미지 파일만 올릴 수 있습니다.");
                    continue;
                }
                String new_file_name=System.nanoTime()+multipartFile.getName()+originalFileExtension;

                // 업로드 한 파일 데이터를 지정한 파일에 저장
                try{
                    file = new File(abPath + path + File.separator + new_file_name);
                    multipartFile.transferTo(file);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new NullPointerException("이미지 저장에 실패했습니다.");
                }
                file.setWritable(true);
                file.setReadable(true);

                images+=abPath + path + File.separator + new_file_name+",";
            }
            // 마지막 콤마는 빼기
            images=images.substring(0,images.length()-1);
            System.out.println(images);
            oriDto=oriDto.of(entity.get());
            oriDto.setPicPath(images);
            AccommodationArticle result=new AccommodationArticle();
            repo.save(result.updateOf(oriDto));
        }
        return oriDto;
    }
}
