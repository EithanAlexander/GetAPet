import React, { useState } from 'react';
import { BASE_URL, CATEGORIES } from './config/config';

/**
 * CreateAd component for creating a new ad for a pet to adopt.
 * This component handles form input, file validation, and form submission to the server.
 *
 * @param {Object} props - The component props. The object being passed from parent component.
 * @param {Object} props.user - The user property inside the props object, contains user details.
 */
function CreateAd({ user }) {
  // State to store form data
  const [formData, setFormData] = useState({
    category: 'Cat', // Default category
    pet_name: '',
    pet_age: '',
    pet_gender: 'Female', // Default gender
    ad_content: '',
    image: null
  });

  // State for showing error messages
  const [error, setError] = useState('');

  const AD_CONTENT_PLACEHOLDER = "Meet Whiskers, a playful and affectionate one-year-old with a shimmering grey coat, ready for cozy cuddles and adventurous play. Fully vaccinated and neutered, this curious explorer loves chasing toys and snuggling on laps. Bring home the joy—Whiskers is waiting for their forever family!"

  /**
   * Handle input changes for text inputs.
   * @param {Object} e - The event object.
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  /**
   * Handles file change for image upload.
   * Validates the file size before updating the form data.
   * @param {Object} e - The event object.
   */
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file && file.size > 1048576) { // Validate file size (1 MB = 1048576 bytes -> 1024 * 1024)
      setError('File size should not exceed 1MB, please select a different image');
    } else {
      setError('');
      setFormData(prevState => ({
        ...prevState,
        image: file
      }));
    }
  };

  // State for managing popup visibility and message
  const [popupSuccessfullyUploaded, setPopup] = useState({ show: false, message: '' });

  /**
   * Handles form submission.
   * Sends form data to the server to create a new ad.
   * @param {Object} e - The event object.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!error) {
      console.log("Submitting new ad", formData);

      const formDataObj = new FormData();
      formDataObj.append('user_id', user.id);
      formDataObj.append('user_name', user.username);
      formDataObj.append('category', formData.category);
      formDataObj.append('pet_name', formData.pet_name);
      formDataObj.append('pet_age', formData.pet_age);
      formDataObj.append('pet_gender', formData.pet_gender);
      formDataObj.append('ad_content', formData.ad_content);
      if (formData.image) {
        formDataObj.append('image', formData.image);
      }

      try {
        const response = await fetch(`${BASE_URL}/create_new_ad`, {
          method: 'PUT',
          body: formDataObj,
        });
        const responseBody = await response.text();
        if (response.ok) {
          console.log('Ad was created successfully, response:', responseBody);
          setPopup({ show: true, message: 'Ad was created successfully' });
        } else {
          setPopup({ show: true, message: responseBody });
        }
      } catch (error) {
        console.error('Error during ad creation:', error);
        setPopup({ show: true, message: 'Failed to create ad ' + error });
      } finally {
        setTimeout(() => {
          setPopup({ show: false, message: '' });
        }, 3000); // 3000 ms = 3 seconds
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Create Ad</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      <label>
        Category:
        <select name="category" value={formData.category} onChange={handleChange}>
          {CATEGORIES.map((category) => (
            <option key={category} value={category}>
              {category}
            </option>
          ))}
        </select>
      </label>
      <br />
      <label>
        Pet Name:
        <input type="text" name="pet_name" value={formData.pet_name} onChange={handleChange} required />
      </label>
      <br />
      <label>
        Pet Age:
        <input type="number" name="pet_age" value={formData.pet_age} onChange={handleChange} min="0" max="100" required />
      </label>
      <br />
      <label>
        Pet Gender:
        <select name="pet_gender" value={formData.pet_gender} onChange={handleChange} required>
          <option value="Female">Female</option>
          <option value="Male">Male</option>
        </select>
      </label>
      <br />
      <label>
        Ad Content:
        <textarea name="ad_content" value={formData.ad_content} onChange={handleChange} required maxLength="500" rows="10" cols="50" placeholder={AD_CONTENT_PLACEHOLDER} />
      </label>
      <br />
      <label>
        Upload image max 1MB (jpg/jpeg/png):
        <input type="file" onChange={handleFileChange} accept="image/*" />
      </label>
      <br />
      {popupSuccessfullyUploaded.show && (
        <div style={{
          position: 'absolute',
          top: '20%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          backgroundColor: '#f8f9fa',
          padding: '20px',
          borderRadius: '5px',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
        }}>
          {popupSuccessfullyUploaded.message}
        </div>
      )}
      <button
        type="submit"
        disabled={!user}
        title={!user ? "Please login to create an ad" : ""}
        style={{
          backgroundColor: !user ? '#ccc' : '#f0f0f0',
          cursor: !user ? 'not-allowed' : 'pointer',
          border: '1px solid #707070',
        }}
      >
        Create Ad
      </button>
    </form>
  );
}

export default CreateAd;
