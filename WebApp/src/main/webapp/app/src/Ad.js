import React from 'react';
import logo from './media/logo.png';

/**
 * Ad Component
 *
 * This component represents a single ad card for a pet. It displays the pet's name,
 * gender, category, age, ad content, and user's contact information. It also includes buttons for deleting
 * and mark ad as a favorite.
 *
 * @param {Object} props - The properties object.
 * @param {Object} props.ad - The ad data.
 * @param {string} props.ad.pet_name - The name of the pet.
 * @param {string} props.ad.pet_gender - The gender of the pet.
 * @param {string} props.ad.category - The category of the pet.
 * @param {number} props.ad.pet_age - The age of the pet.
 * @param {string} props.ad.ad_content - The content of the ad.
 * @param {string} props.ad.image_path - The image path for the pet's photo.
 * @param {string} props.ad.display_name - The display name of the ad's author.
 * @param {string} props.ad.email - The email address of the ad's author.
 * @param {string} props.ad.phone - The phone number of the ad's author.
 * @param {Date} props.ad.created_at - The timestamp of when the ad was created, according to GMT timezone.
 * @param {Function} [props.handleDelete] - The function to call when the delete button is clicked.
 * @param {Function} [props.handleFavorite] - The function to call when the favorite button is clicked.
 * @param {Object} [props.user] - The user object representing the logged-in user (ad's author).
 * @param {string} props.user.id - The ID of the logged-in user (ad's author).
 *
 * @returns {JSX.Element} The rendered ad component.
 */
const Ad = ({ ad, handleDelete, handleFavorite, user }) => {
  return (
    <div style={{ border: '1px solid #add8e6', margin: '10px', padding: '10px' }}>
      <h3>Hey! My name is {ad.pet_name} and I am a {ad.pet_gender} {ad.category} at the age of {ad.pet_age}</h3>
      <div>
        <p><strong>Ad content</strong></p>
        <p>{ad.ad_content.split('\n').map((line, i) => <span key={i}>{line}<br /></span>)}</p>
      </div>
      <img src={ad.image_path ? `http://localhost:8080${ad.image_path}` : logo} alt="Ad" style={{ width: '200px', height: 'auto', opacity: ad.image_path ? 1 : 0.5 }} />
      <div>
        <strong>Contact:</strong> {user ? `${ad.display_name} - ${ad.email} - ${ad.phone}` : <strong style={{color: 'red'}}>Please login to view contact details</strong>}
      </div>
      <div>
        <strong>Posted (US date format):</strong> {new Date(ad.created_at).toLocaleString('en-US', { year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric' })}
      </div>
      {handleDelete && (
        <button onClick={() => handleDelete(ad.ad_id, ad.image_path)}>Remove</button>
      )}
      {handleFavorite && user && (
        <button onClick={() => handleFavorite(ad.ad_id)}>Favorite</button>
      )}
    </div>
  );
};

export default Ad;
