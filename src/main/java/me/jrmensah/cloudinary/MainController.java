package me.jrmensah.cloudinary;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class MainController {
    @Autowired
    ActorRepository actorRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("gotmovies", movieRepository.count());
        model.addAttribute("gotactors", actorRepository.count());
        model.addAttribute("actorList", actorRepository.findAll());
        model.addAttribute("movieList", movieRepository.findAll());
        model.addAttribute("title", "Movie Database");
        return "index";
    }

    @GetMapping("/addmovie")
    public String addMovie(Model model) {
        Movie movie = new Movie();
        model.addAttribute("movie", movie);
        return "addmovie";
    }

    @PostMapping("/addmovie")
    public String saveMovie(@ModelAttribute ("movie") Movie movie) {
        movieRepository.save(movie);
            return "redirect:/";
        }
    @GetMapping("/addactor")
    public String addActor(Model model) {
        model.addAttribute("actor", new Actor());
        return "addactor";
    }

    @PostMapping("/addactor")
    public String saveActor(@ModelAttribute ("actor") Actor actor, MutlipartHttpServletRequest request) {
        MultipartFile f = request.getFile("file");
        if(f.isEmpty()){
        return "redirect:/addactor";
    }
        try{
            Map uploadResult = cloudc.upload(f.getBytes(),ObjectUtils.asMap("resourcetype", "auto"));
            String uploadURL = (String) uploadResult.get("url");
            String uploadedName = (String) uploadResult.get("public_id");
            String transformedImage= cloudc.createUrl(uploadedName);
            System.out.println(transformedImage);
            System.out.println("Uploaded:" + uploadURL);
            System.out.println("Name:" + uploadedName);
            actor.setHeadshot(transformedImage);
            actorRepository.save(actor);
        }catch(IOException e){
            e.printStackTrace();
            return "redirect:/addactor";
        }
        return "redirect:/";


    }
    @GetMapping("/addactorstomovie/{id}")
    public String addActor(@PathVariable("id") long movieID, Model model) {
        Movie thisMovie = movieRepository.findOne(new Long(movieID));
        Iterable actorsInMovie = thisMovie.getCast();

        model.addAttribute("mov", thisMovie);
        model.addAttribute("actorList", actorRepository.findAllByMoviesNotContaining(thisMovie));
        return "movieaddactor";
}

    @PostMapping("/addactorstomovie/{id}")
    public String addActorsToMovie(@RequestParam("movie") long movieID,
                                   @PathVariable("actorid") String actorID, @ModelAttribute("aMovie") Movie m, Model model){
        Actor a= actorRepository.findOne(new Long(actorID));
        a.addMovie(movieRepository.findOne(new Long(actorID)));
        actorRepository.save(a);
        model.addAttribute("actor", actorRepository.findAll());
        model.addAttribute("movieList", movieRepository.findAll());
        return "redirect:/";
    }
    @GetMapping("/addmoviestoactor/{id}")
    public String addMoviesToActor(@RequestParam("id") long actorID, Model model)
    {

        model.addAttribute("actor", actorRepository.findOne(new Long(actorID)));
        model.addAttribute("movieList", movieRepository.findAll());
        return "redirect:/";
    }
    @PostMapping("/addmoviestoactor/{movid}")
    public String addMoviesToActor(@RequestParam("actors") String actorID,
                                   @PathVariable("movid") long movieID, @ModelAttribute("anActor") Actor a, Model model){
        Movie m= movieRepository.findOne(new Long(movieID));
        m.addActor(actorRepository.findOne(new Long(actorID)));
        movieRepository.save(m);
        model.addAttribute("actorList", actorRepository.findOne(new Long(actorID)));
        model.addAttribute("movieList", actorRepository.findAll());
        return "movieaddactor";
    }

    @RequestMapping("/search")
    public String SearchResult(){
        //Get actors matching a string
        Iterable<Actor> actors =
                actorRepository.findAllByRealnameContainingIgnoreCase("Sandra");

        for (Actor a : actors){
            System.out.println(a.getName());
        }

        //Show the movies the actors were in
        for(Movie m: movieRepository.findAllByCastIsIn(actors)){
            System.out.println(m.getTitle());
        }
        return "redirect:/";
    }

    private class MutlipartHttpServletRequest {
        public void getFile(String file) {
        }
    }
}

