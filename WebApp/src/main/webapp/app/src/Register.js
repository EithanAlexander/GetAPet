import React, { useState } from 'react';
import puppiesRunning from './media/puppiesRunning.gif';
import { useNavigate } from 'react-router-dom';
import { BASE_URL } from './config/config';

/**
 * Register component handles user registration.
 * It provides a form for user input and handles form submission,
 * including form validation and API communication.
 */
function Register() {
  const navigate = useNavigate();

  // State to store input values
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    display_name: '',
    email: '',
    phone: ''
  });

  // State to toggle password visibility
  const [passwordShown, setPasswordShown] = useState(false);

  /**
   * Toggles the visibility of the password input field.
   */
  const togglePasswordVisibility = () => {
    setPasswordShown(!passwordShown);
  };

  /**
   * Handles input change and updates formData state.
   *
   * @param {Object} e - The event object from input change.
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  // State for managing popup visibility and message
  const [popupSuccessfullyRegistered, setPopup] = useState({ show: false, message: '' });

  /**
   * Handles form submission and communicates with the backend API.
   *
   * @param {Object} e - The event object from the form submission.
   */
  const handleSubmit = async (e) => {
    e.preventDefault();  // Prevent default form submission behavior so it won't refresh and lose user's data
    const formDataObj = new FormData();
    formDataObj.append('username', formData.username);
    formDataObj.append('password', formData.password);
    formDataObj.append('display_name', formData.display_name);
    formDataObj.append('email', formData.email);
    formDataObj.append('phone', formData.phone);

    try {
      const response = await fetch(`${BASE_URL}/register`, {
        method: 'PUT',
        body: formDataObj
      });
      if (response.ok) {
        console.log('Registered successfully');
        setPopup({ show: true, message: 'Registered successfully' });
        // Set a timeout to hide the popup and redirect to login page after a few seconds
        setTimeout(() => {
          navigate('/login');
          setPopup({ show: false, message: '' });
        }, 3000);
      } else {
        const errorMsg = await response.text(); // Use response.text() for plain text response
        console.log('Registration failed:', errorMsg);
        setPopup({ show: true, message: errorMsg });
      }
    } catch (error) {
      console.error('Network or other error', error);
      setPopup({ show: true, message: 'Failed to register due to a network or system error.' });
    } finally {
      // Automatically hide the popup after 3 seconds
      setTimeout(() => {
        setPopup({ show: false, message: '' });
      }, 3000);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Register</h2>
      <label>
        Username:
        <input type="text" name="username" value={formData.username} onChange={handleChange} required />
      </label>
      <br />
      <label>
        Password:
        <input type={passwordShown ? "text" : "password"} name="password" value={formData.password} onChange={handleChange} required />
        <button type="button" onClick={togglePasswordVisibility}>
          {passwordShown ? "Hide" : "Show"}
        </button>
      </label>
      <br />
      <label>
        Display Name:
        <input type="text" name="display_name" value={formData.display_name} onChange={handleChange} required />
      </label>
      <br />
      <label>
        Email:
        <input type="email" name="email" value={formData.email} onChange={handleChange} required />
      </label>
      <br />
      <label>
        Phone:
        <input type="tel" name="phone" value={formData.phone} onChange={handleChange} required />
      </label>
      <br />
      {popupSuccessfullyRegistered.show && (
        <div style={{
          position: 'absolute',
          top: '20%', // Adjust positioning as needed
          left: '50%',
          transform: 'translate(-50%, -50%)',
          backgroundColor: '#f8f9fa',
          padding: '20px',
          borderRadius: '5px',
          boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
        }}>
          {popupSuccessfullyRegistered.message}
        </div>
      )}
      <button type="submit">Submit</button>

      <p style={{ marginTop: '30px', color: 'blue' }}>Already have a user? You can <a href="login">login</a> right here.</p>
      <img src={puppiesRunning} alt="Puppies" tabIndex="1" style={{ width: '200px', height: 'auto', marginRight: 'auto'}} />
    </form>
  );
}

export default Register;
