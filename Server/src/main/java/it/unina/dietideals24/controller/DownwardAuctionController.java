package it.unina.dietideals24.controller;

import it.unina.dietideals24.dto.DownwardAuctionDto;
import it.unina.dietideals24.enumeration.CategoryEnum;
import it.unina.dietideals24.model.DietiUser;
import it.unina.dietideals24.model.DownwardAuction;
import it.unina.dietideals24.service.interfaces.IDietiUserService;
import it.unina.dietideals24.service.interfaces.IDownwardAuctionService;
import it.unina.dietideals24.service.interfaces.IImageService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/downward-auctions")
public class DownwardAuctionController {


    private static final String DOWNWARD_AUCTION_IMAGE_DIRECTORY = "downward_auction";
    @Qualifier("mainDownwardAuctionService")
    private final IDownwardAuctionService downwardAuctionService;
    @Qualifier("mainDietiUserService")
    private final IDietiUserService dietiUserService;
    private final DownwardAuctionTimerController downwardAuctionTimerController;
    @Qualifier("locallyStoreImageService")
    private final IImageService imageService;

    public DownwardAuctionController(IDownwardAuctionService downwardAuctionService,
                                     IDietiUserService dietiUserService,
                                     DownwardAuctionTimerController downwardAuctionTimerController,
                                     IImageService imageService) {
        this.downwardAuctionService = downwardAuctionService;
        this.dietiUserService = dietiUserService;
        this.downwardAuctionTimerController = downwardAuctionTimerController;
        this.imageService = imageService;
    }

    @GetMapping
    public List<DownwardAuction> getDownwardAuctions() {
        return downwardAuctionService.getDownwardAuctions();
    }

    @GetMapping("first-six")
    public List<DownwardAuction> getFirst6DownwardAuctions() {
        return downwardAuctionService.getFirst6DownwardAuctions();
    }

    @GetMapping("{id}")
    public DownwardAuction getDownwardAuctionById(@PathVariable("id") Long id) {
        return downwardAuctionService.getDownwardAuctionById(id);
    }

    @GetMapping("owner/{id}")
    public List<DownwardAuction> getDownwardAuctionsByOwner(@PathVariable("id") Long ownerId) {
        return downwardAuctionService.getDownwardAuctionsByOwner(ownerId);
    }

    @GetMapping("/category/{category}")
    public List<DownwardAuction> getDownwardAuctionsByCategory(@PathVariable("category") CategoryEnum category) {
        return downwardAuctionService.getDownwardAuctionsByCategory(category);
    }

    @GetMapping("/search/{keyword}")
    public List<DownwardAuction> getDownwardAuctionsByKeyword(@PathVariable("keyword") String keyword) {
        return downwardAuctionService.getByKeyword(keyword);
    }

    @PostMapping("/create")
    public ResponseEntity<DownwardAuction> createDownwardAuction(@RequestBody DownwardAuctionDto downwardAuctionDto) throws BadRequestException {
        if (dietiUserService.existsById(downwardAuctionDto.getOwnerId())) {
            DietiUser owner = dietiUserService.getUserById(downwardAuctionDto.getOwnerId());
            DownwardAuction createdDownwardAuction = downwardAuctionService.save(downwardAuctionDto, owner);

            downwardAuctionTimerController.startNewTimer(createdDownwardAuction);

            return ResponseEntity.ok(createdDownwardAuction);
        } else {
            throw new BadRequestException("User not found");
        }
    }

    @PostMapping("{id}/image")
    public void updateDownwardAuctionImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) throws BadRequestException {
        if (downwardAuctionService.existsById(id)) {
            try {
                imageService.saveImage(DOWNWARD_AUCTION_IMAGE_DIRECTORY, id, image);
                downwardAuctionService.linkImage(DOWNWARD_AUCTION_IMAGE_DIRECTORY, id);
            } catch (IOException e) {
                throw new BadRequestException("Could not upload image");
            }
        } else
            throw new BadRequestException("Auction doesn't exist");
    }
}
