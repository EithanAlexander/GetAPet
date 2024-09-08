import React from 'react';
import smartCat from './media/smartCat.jpg';

/**
 * Guides component that provides useful links and resources for pet owners.
 * This component includes links to external guides and articles on various pet-related topics.
 */
function Guides() {
  const vetLink = "https://www.doctordolittle.co.il/Suppliers/2/%D7%95%D7%98%D7%A8%D7%99%D7%A0%D7%A8%D7%99%D7%9D.html";
  const dogAdoptionGuide = "https://spca.co.il/wp-content/uploads/2016/11/%D7%94%D7%9E%D7%93%D7%A8%D7%99%D7%9A-%D7%9C%D7%90%D7%99%D7%9E%D7%95%D7%A5-%D7%9B%D7%9C%D7%91%D7%99%D7%9D-%D7%A1%D7%95%D7%A4%D7%99.pdf";
  const catCantEat = "https://www.animal-hospital.co.il/cats/%D7%90%D7%95%D7%9B%D7%9C-%D7%90%D7%A1%D7%95%D7%A8-%D7%9C%D7%97%D7%AA%D7%95%D7%9C/";
  const weirdPets = "https://crazyrichpets.com/unique-weird-pets/";
  const movieRecommendation = "https://www.youtube.com/watch?v=i-80SGWfEjM&ab_channel=Illumination";
  const petBenefits = "https://www.animalfriends.co.uk/dog/dog-blog/10-benefits-of-owning-a-pet/";

  return (
    <div>
      <h2>Guides</h2>
      <p>We're sure you have many questions after adding a furry friend to your family.</p>
      <p>To help with that, we grouped together a few articles and guides regarding common topics.</p>
      <p>Please let us know if something important is missing!</p>

      <p style={{ marginTop: '40px' }}>1. <a href={vetLink} target="_blank" rel="noopener noreferrer">list of veterinarians in Israel</a></p>
      <p>2. <a href={dogAdoptionGuide} target="_blank" rel="noopener noreferrer">The full guide for adopting a dog</a></p>
      <p>3. <a href={catCantEat} target="_blank" rel="noopener noreferrer">What your cat can't eat?</a></p>
      <p>4. <a href={weirdPets} target="_blank" rel="noopener noreferrer">What are the most bizarre pets in the world?</a></p>
      <p>5. <a href={movieRecommendation} target="_blank" rel="noopener noreferrer">It's movie time! Have you watched The Secret Life Of Pets yet?</a></p>
      <p>6. <a href={petBenefits} target="_blank" rel="noopener noreferrer">10 benefits of having a pet!</a></p>
      <img src={smartCat} alt="A smart cat" style={{ width: '200px', height: 'auto', marginTop: '0px' }} />
    </div>
  );
}

export default Guides;
