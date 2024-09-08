import React, { useCallback } from 'react';
import { BASE_URL, ADS_PER_PAGE } from './config/config';
import Ad from './Ad';
import usePagination from './hooks/usePagination';

/**
 * MyFavorites component for displaying and managing the user's favorite advertisements.
 * This component handles fetching, displaying, and deleting favorite ads with pagination support.
 *
 * @param {Object} props - The component props. The object being passed from parent component.
 * @param {Object} props.user - The user property inside the props object, contains user details.
 */
function MyFavorites({ user }) {
  /**
   * Fetches the user's favorite ads from the server with pagination.
   * @param {number} pageNum - The current page number.
   * @param {number} adsPerPage - The number of ads to display per page.
   * @returns {Promise<Object>} - An object containing the ads data and total ads count.
   * @throws {Error} - If the request fails.
   */
  const fetchFavoritesAds = useCallback(async (pageNum, adsPerPage) => {
    const response = await fetch(`${BASE_URL}/get_user_favorites_ads?user_id=${encodeURIComponent(user.id)}&pageNum=${pageNum}&adsPerPage=${adsPerPage}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) throw new Error('Failed to fetch favorite ads');
    const data = await response.json();
    return { data: data.ads, totalAds: data.totalAds };
  }, [user.id]);

  const {
    data: ads,
    setData: setAds,
    loading,
    error,
    setError,
    currentPage,
    adsPerPage,
    totalAds,
    handleNextPage,
    handlePreviousPage,
    handleAdsPerPageChange,
  } = usePagination(fetchFavoritesAds);

  /**
   * Handles removing an ad from the user's favorites on the server.
   * @param {number} user_id - The ID of the user.
   * @param {number} ad_id - The ID of the ad to delete.
   */
  const handleDelete = async (user_id, ad_id) => {
    try {
      const response = await fetch(`${BASE_URL}/delete_ad_from_favorites?user_id=${encodeURIComponent(user_id)}&ad_id=${encodeURIComponent(ad_id)}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
      });
      if (response.ok) {
        setAds((prevAds) => prevAds.filter((ad) => ad.ad_id !== ad_id));
      } else {
        throw new Error('Failed to delete ad');
      }
    } catch (error) {
      console.error('Error deleting ad:', error);
      setError('Could not delete the ad, please try again');
    }
  };

  return (
    <div>
      <h2>My Favorites</h2>
      {loading ? (
        <p>Loading ads...</p>
      ) : error ? (
        <p>Error: {error}</p>
      ) : (
        <div>
          <div>
            <label htmlFor="adsPerPage">Ads per page: </label>
            <select id="adsPerPage" value={adsPerPage} onChange={handleAdsPerPageChange}>
              {ADS_PER_PAGE.map((num) => (
                <option key={num} value={num}>{num}</option>
              ))}
            </select>
          </div>
          <p><strong>My favorite ads</strong></p>
          {ads.map((ad, index) => (
            <Ad key={index} ad={ad} handleDelete={(adId) => handleDelete(user.id, adId)} user={user} />
          ))}
          <div>
            <button onClick={handlePreviousPage} disabled={currentPage === 1}>Previous</button>
            <span> Page {currentPage} of {Math.ceil(totalAds / adsPerPage) || 1} </span>
            <button onClick={handleNextPage} disabled={currentPage >= Math.ceil(totalAds / adsPerPage)}>Next</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default MyFavorites;
